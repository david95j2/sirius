package com.example.sirius.user.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteUserReq {
    @NotEmpty(message = "비밀번호는 필수 입력값입니다. 키가 password인지 확인해주세요.")
    private String password;
}
