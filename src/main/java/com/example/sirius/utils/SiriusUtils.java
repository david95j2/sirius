package com.example.sirius.utils;


import com.example.sirius.exception.AppException;
import com.example.sirius.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.json.simple.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SiriusUtils {
    public static Resource loadFileAsResource(String filePath, String fileName) {
        try {
            Path fileStorageLocation = Paths.get(filePath).toAbsolutePath().normalize();
            Path targetPath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(targetPath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " +filePath+" "+ fileName);
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
}
