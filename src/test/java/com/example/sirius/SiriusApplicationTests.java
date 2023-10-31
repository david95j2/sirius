package com.example.sirius;

import com.example.sirius.ftp.domain.PostMapURL;
import com.example.sirius.map.domain.PostMapReq;
import com.example.sirius.utils.SiriusUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootTest
class SiriusApplicationTests {

    @Test
    void contextLoads() {
//        String location = "여수";
//        String unicode = SiriusUtils.stringToUnicode(location).replace("\\","");
//        String decode_location = SiriusUtils.addBackslashAndDecodeUsingSplit(unicode);
//        System.out.println(decode_location);
        if (true) {
            System.out.println("1");
        } else if (true) {
            System.out.println("2");
        }
    }

    @Test
    void test() {
        String location = "인제";

    }


}
