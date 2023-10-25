package com.example.sirius.utils;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SiriusUtils {
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

    public static ResponseEntity getFile(Resource file, Boolean isThumbnail) throws IOException {

        String fileName = file.getFilename();
//        System.out.println((file.contentLength() / 1024) / 1024 );
        // Assume that the file extension is everything after the last dot
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        String mediaType = getMediaTypeForExtension(fileExtension);
        if (mediaType == null) {
            throw new RuntimeException("Could not determine file type.");
        }

        try {
            FileSystemResource resource;
            if (isThumbnail) {
                // Create a temporary file to store the thumbnail
                File thumbnail = File.createTempFile("thumbnail", "." + fileExtension);
                try (InputStream in = new FileInputStream(file.getFile())) {
                    // Use Thumbnailator to create the thumbnail
                    Thumbnails.of(in)
                            .size(320, 200)  // Set the dimensions of the thumbnail. Adjust as needed.
                            .toFile(thumbnail);
                }
                resource = new FileSystemResource(thumbnail);
            } else {
                resource = new FileSystemResource(file.getFile());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
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
                            jsonObject.put(key, Integer.parseInt(value)); // 정수로 변환
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
        commandList.add(pythonPath);
        if (gpuNum != null) {
            commandList.add(gpuNum);
        }
        commandList.add(scriptPath);
        commandList.addAll(args);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();
            if (type.equals("modifier.py")) {
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
            } else {
                readStream(process.getInputStream(), "Output", false, type);
            }
            readStream(process.getErrorStream(), "Error", true, type);
            int exitCode = process.waitFor();
            log.info("[Python "+type+" Program] Exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            log.error("[Python "+type+" Program] Error occurred while executing external process", e);
        }
        return output.toString();
    }

    private static void readStream(InputStream inputStream, String type, boolean isError, String scriptype) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isError) {
                    log.error("[Python "+scriptype+" Program] " + type + " message: " + line);
                } else {
                    log.info("[Python "+scriptype+" Program] " + type + " message: " + line);
                }
            }
        } catch (IOException e) {
            log.error("[Python "+scriptype+" Program] Error occurred while reading " + type + " stream", e);
        }
    }

    public static List<String> listFilesInDirectoryNIO(String path) throws IOException {
        return Files.list(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public static List<String> listFilesMatchingPattern(String path, String pattern) throws IOException {
        return Files.list(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().matches(pattern))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
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
        Rotation rotation = new Rotation(q0, q1, q2, q3, true);
        double[] euler = rotation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM);
        return euler;
    }
}
