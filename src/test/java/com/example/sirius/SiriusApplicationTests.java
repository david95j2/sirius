package com.example.sirius;

import com.example.sirius.ftp.domain.PostMapURL;
import com.example.sirius.map.domain.PostMapReq;
import com.example.sirius.utils.SiriusUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootTest
class SiriusApplicationTests {

    @Test
    public void albumUpload() throws IOException {

        Path targetPath = Paths.get("D:/Bentely/data/20240226-29_yd/gryphon").normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(targetPath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (resource.exists()) {
            if (resource.getFile().isDirectory()) {
                System.out.println("folder");
            } else {
                System.out.println("files");
            }
        } else {
            System.out.println("Empty");
        }
    }
}
