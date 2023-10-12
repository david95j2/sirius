package com.example.sirius.drone.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostDroneReq {
    @NotNull(message = "최소전압값은 필수 입력값입니다. 키가 min인지 확인해주세요.")
    private Float min;
    @NotNull(message = "최대전압값은 필수 입력값입니다. 키가 max인지 확인해주세요.")
    private Float max;
    @NotBlank(message = "드론명은 필수 입력값입니다. 키가 name인지 확인해주세요.")
    private String name;
    @NotNull(message = "x_dimension는 필수 입력값입니다. 키가 x_dimension인지 확인해주세요.")
    private Integer x_dimension;
    @NotNull(message = "y_dimension는 필수 입력값입니다. 키가 y_dimension인지 확인해주세요.")
    private Integer y_dimension;
    @NotNull(message = "z_dimension는 필수 입력값입니다. 키가 z_dimension인지 확인해주세요.")
    private Integer z_dimension;
}
