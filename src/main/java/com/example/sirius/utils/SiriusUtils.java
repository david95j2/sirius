package com.example.sirius.utils;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.sirius.album.analysis.domain.JsonModel;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;



import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SiriusUtils {
    private static final String EXIF_TOOL_PATH = "exiftool";
    public static Resource loadFileAsResource(String filePath, String fileName) {
        try {
            Path fileStorageLocation = Paths.get(filePath).toAbsolutePath().normalize();
            Path targetPath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(targetPath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filePath + " " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("[MalformedURLException Error] File not found " + fileName, ex);
        }
    }

    public static ResponseEntity getFile(Resource file, Boolean isThumbnail, Boolean resize) throws IOException {

        String fileName = file.getFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        String mediaType = getMediaTypeForExtension(fileExtension);
        if (mediaType == null) {
            throw new RuntimeException("Could not determine file type.");
        }

        try {
            FileSystemResource resource;
            if (isThumbnail) {
                File thumbnail = File.createTempFile("thumbnail", "." + fileExtension);
                try (InputStream in = new FileInputStream(file.getFile())) {
                    Thumbnails.of(in).size(320, 200).toFile(thumbnail);
                }
                resource = new FileSystemResource(thumbnail);
            } else {
                if (resize) {
                    File resized = File.createTempFile("resized", "." + fileExtension);
                    try (InputStream in = new FileInputStream(file.getFile())) {
                        Thumbnails.of(in).size(2100, 1400).toFile(resized);
                    }
                    resource = new FileSystemResource(resized);
                } else {
                    resource = new FileSystemResource(file.getFile());
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType(mediaType)).body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading file " + fileName, e);
        }
    }

    private static String getMediaTypeForExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pcd":
                return "application/octet-stream";
            case "json":
                return "application/json";
            default:
                return null;
        }
    }

    public static JSONObject convertTxtToJson(String file_path) {
        JSONObject jsonObject = new JSONObject();
        File file = new File(file_path);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" : ");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if ("number of pointcloud".equals(key)) {
                            jsonObject.put(key, Integer.parseInt(value));
                        } else {
                            try {
                                jsonObject.put(key, Float.parseFloat(value));
                            } catch (NumberFormatException e) {
                                jsonObject.put(key, value);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonObject;
        } else {
            throw new AppException(ErrorCode.FTP_INFO_NOT_FOUND);
        }
    }

    public static Map<String, String> extractImageMetadata(String filePath) {
        Map<String, String> metadataMap = new HashMap<>();

        try {
            File imageFile = new File(filePath);
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    metadataMap.put(tag.getTagName(), tag.getDescription());
                }
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        return metadataMap;
    }

    public static String joinArrayWithComma(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            if (i > 0) sb.append(",");
            sb.append(array.getInt(i));
        }
        return sb.toString();
    }

    public static String executePythonScript(String pythonPath, String scriptPath, List<String> args, String type, String gpuNum) {
        List<String> commandList = new ArrayList<>();
        if (pythonPath != null) {
            commandList.add(pythonPath);
        }
        if (gpuNum != null) {
            commandList.add(gpuNum);
        }
        commandList.add(scriptPath);
        commandList.addAll(args);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();
            if (type.equals("calcDistance")) {
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }
                }
            } else {
                readStream(process.getInputStream(), "Output", false, type);
            }
            readStream(process.getErrorStream(), "Error", true, type);
            int exitCode = process.waitFor();
            log.info("[Python " + type + " Program] Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            log.error("[Python " + type + " Program] Error occurred while executing external process", e);
        }
        return output.toString();
    }

    private static void readStream(InputStream inputStream, String type, boolean isError, String scriptype) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isError) {
                    log.error("[Python " + scriptype + " Program] " + type + " message: " + line);
                } else {
                    log.info("[Python " + scriptype + " Program] " + type + " message: " + line);
                }
            }
        } catch (IOException e) {
            log.error("[Python " + scriptype + " Program] Error occurred while reading " + type + " stream", e);
        }
    }

    public static List<String> listFilesInDirectoryNIO(String path) throws IOException {
        return Files.list(Paths.get(path)).filter(Files::isRegularFile).sorted().map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
    }

    public static List<String> listFilesMatchingPattern(String path, String pattern) throws IOException {
        return Files.list(Paths.get(path)).filter(Files::isRegularFile).filter(p -> p.toString().matches(pattern)).sorted().map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
    }

    public static JSONObject validateMessageForAlbumId(String payload, WebSocketSession session) throws Exception {
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException e) {
            session.sendMessage(new TextMessage("[Error] : Invalid JSON format!"));
            return null;
        }

        if (!jsonObject.has("albumId")) {
            session.sendMessage(new TextMessage("[Error] : JSON should have an 'albumId' key!"));
            return null;
        }

        return jsonObject;
    }

    public static JSONObject validateMessageForRemoveIdAndMaskId(String payload, WebSocketSession session) throws Exception {
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(payload);
        } catch (JSONException e) {
            session.sendMessage(new TextMessage("[Error] : Invalid JSON format!"));
            return null;
        }

        if (!jsonObject.has("remove_id") || !jsonObject.has("maskId")) {
            session.sendMessage(new TextMessage("[Error] : JSON should have 'remove_id' and 'maskId' keys!"));
            return null;
        }

        JSONArray removeIdArray = jsonObject.optJSONArray("remove_id");
        if (removeIdArray == null) {
            session.sendMessage(new TextMessage("[Error] : 'remove_id' value should be an array!"));
            return null;
        }

        return jsonObject;
    }

    public static void makeFolder(File folder_path) {

        if (!folder_path.exists()) { // 폴더 없으면
            try {
                folder_path.mkdirs();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public static double[] quaternionToEuler(double q0, double q1, double q2, double q3) {
        Rotation rotation = new Rotation(q3, q0, q1, q2, true);
        //Rotation rotation = new Rotation(q0, q1, q2, q3, true);
        double[] euler = rotation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM);
        return euler;
    }

    public static void saveImageToFile(InputStream imageZipStream, File extractedImage) throws IOException {
        // FileOutputStream을 이용하여 이미지를 디스크에 저장하기 위한 스트림을 생성.
        // 이 스트림은 'extractedImage' 파일에 데이터를 쓸 것.
        try (BufferedOutputStream imageOutputStream = new BufferedOutputStream(new FileOutputStream(extractedImage))) {
            // 버퍼는 압축 해제된 이미지 데이터를 임시로 저장하기 위함.
            byte[] buffer = new byte[1024];
            int bytesRead;
            // 이미지 압축 해제 스트림(imageZipStream)에서 버퍼 크기만큼 데이터를 읽음.
            // bytesRead는 실제로 읽은 바이트 수를 나타냄.
            // bytesRead가 -1이면, 스트림의 끝에 도달했음을 의미.
            while ((bytesRead = imageZipStream.read(buffer)) != -1) {
                // imageOutputStream을 이용하여 버퍼에 저장된 데이터를 'extractedImage' 파일에 씀.
                // buffer의 0번째 위치에서 bytesRead만큼의 데이터를 씀.
                imageOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static String stringToUnicode(String str) {
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int code = str.codePointAt(i);
            unicode.append(code < 128 ? (char) code : String.format("\\u%04x", code));
        }
        return unicode.toString();
    }

    public static String unicodeToString(String unicode) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < unicode.length(); i++) {
            if (unicode.charAt(i) == '\\' && unicode.charAt(i + 1) == 'u') {
                char ch = (char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
                str.append(ch);
                i += 5;
            } else {
                str.append(unicode.charAt(i));
            }
        }
        return str.toString();
    }

    public static String addBackslashAndDecodeUsingSplit(String str) {
        String[] parts = str.split("u");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < parts.length; i++) {

            sb.append("\\u").append(parts[i]);
        }

        return unicodeToString(sb.toString());
    }

    public static ResponseEntity<InputStreamResource> resizeImageWithMetadataAndSend(Resource originalImageResource, boolean resize) throws IOException, InterruptedException {
        File originalImage = originalImageResource.getFile();
        String originalFileName = originalImage.getName();

        Path tempDir = Files.createTempDirectory("resized-images");
        File processedImage = new File(tempDir.toFile(), originalFileName);

        if (resize) {
            // 이미지 리사이즈
            BufferedImage inputImage = ImageIO.read(originalImage);
            BufferedImage outputImage = new BufferedImage(2100, 1400, inputImage.getType());
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, 2100, 1400, null);
            g2d.dispose();
            ImageIO.write(outputImage, "JPEG", processedImage);

            // ExifTool을 사용하여 원본 메타데이터 복사
            copyMetadataWithExifTool(originalImage, processedImage);

            // 이미지의 가로세로를 업데이트
            updateDimensionsWithExifTool(processedImage, 2100, 1400);
        } else {
            // 리사이즈하지 않는 경우, 원본 이미지를 그대로 사용
            Files.copy(originalImage.toPath(), processedImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + processedImage.getName() + "\"");

        InputStreamResource resource = new InputStreamResource(new FileInputStream(processedImage));
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("image/jpeg"))
                .body(resource);
    }

    private static void copyMetadataWithExifTool(File source, File destination) throws IOException, InterruptedException {
        String command = String.format("%s -TagsFromFile %s %s", EXIF_TOOL_PATH, source.getAbsolutePath(), destination.getAbsolutePath());
        Runtime.getRuntime().exec(command).waitFor();
    }

    private static void updateDimensionsWithExifTool(File image, int width, int height) throws IOException, InterruptedException {
        String command = String.format("%s -ImageWidth=%d -ImageHeight=%d %s", EXIF_TOOL_PATH, width, height, image.getAbsolutePath());
        Runtime.getRuntime().exec(command).waitFor();
    }

    public static void saveResizedImage(MultipartFile file, String os_path, int width, int height) {
        try {
            File outputFile = new File(Paths.get(os_path,file.getOriginalFilename()).toString());
            File parentDir = outputFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            Thumbnails.of(file.getInputStream())
                    .size(width, height)
                    .toFile(outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(String jsonFileName, ArrayList<JsonModel> jsonModel) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 객체를 파일로 저장
            objectMapper.writeValue(new File(jsonFileName), jsonModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
