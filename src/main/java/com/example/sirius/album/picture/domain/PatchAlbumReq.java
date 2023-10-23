package com.example.sirius.album.picture.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PatchAlbumReq {
    @Pattern(regexp = "^\\d{8} \\d{6}$", message = "regdate는 'YYYYMMDD HHMMSS' 형식으로 입력해주세요.")
    private String regdate;
}
