package com.example.sirius;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

@SpringBootTest
class SiriusApplicationTests {

    @Test
    void contextLoads() {
        String korean = "대전";
        String encoded = Base64.getEncoder().encodeToString(korean.getBytes());
        System.out.println(encoded);  // 7J206rWs
    }

}
