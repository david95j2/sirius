package com.example.sirius.configuration;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@Getter
public class WebConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);
    private static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    @Value("${webgcs.cors.user.1}")
    private String user1Ip;

    @Value("${webgcs.cors.user.2}")
    private String user2Ip;

    @Value("${webgcs.cors.user.3}")
    private String user3Ip;

    @Value("${webgcs.cors.user.4}")
    private String user4Ip;

    @Value("${ftp.ip}")
    private String ftpIp;

    @Value("${ftp.port}")
    private String ftpPort;

    @Value("${ftp.id}")
    private String ftpId;

    @Value("${ftp.id.password}")
    private String ftpPassword;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 요청 경로에 대해
                .allowedOrigins(user1Ip, user2Ip,user3Ip,user4Ip)
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","));
    }
}
