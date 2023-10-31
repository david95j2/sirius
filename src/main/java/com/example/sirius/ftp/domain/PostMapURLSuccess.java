package com.example.sirius.ftp.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMapURLSuccess {
    @NotBlank(message = "장소는 필수 입력값입니다. 키가 location인지 확인해주세요.")
    private String location;
    @NotBlank(message = "날짜는 필수 입력값입니다. 키가 regdate인지 확인해주세요.")
    @Pattern(regexp = "^\\d{8}_\\d{6}$", message = "regdate은 'yyyyMMdd_HHmmss' 형식을 지켜야 합니다.")
    private String regdate;
}
