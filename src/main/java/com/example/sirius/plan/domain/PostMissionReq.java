package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMissionReq {
    @NotBlank(message = "이름은 필수 입력값입니다. 키가 name인지 확인해주세요.")
    private String name;
    @NotNull(message = "group_num은 필수 입력값입니다. 키가 group_num인지 확인해주세요.")
    private Integer group_num;
}
