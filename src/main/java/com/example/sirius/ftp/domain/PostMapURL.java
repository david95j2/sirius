package com.example.sirius.ftp.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMapURL {
    @NotBlank(message = "장소는 필수 입력값입니다. 키가 location인지 확인해주세요.")
    private String location;
    @NotBlank(message = "날짜는 필수 입력값입니다. 키가 datetime인지 확인해주세요.")
    private String regdate;
}
