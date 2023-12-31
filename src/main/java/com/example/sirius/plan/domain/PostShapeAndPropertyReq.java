package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostShapeAndPropertyReq {
    @NotBlank(message = "도형은 필수 입력값입니다. 키가 shape인지 확인해주세요.")
    private String shape;
    @NotNull(message = "seq은 필수 입력값입니다. 키가 seq인지 확인해주세요.")
    private Integer seq;
    @NotNull(message = "from_wall은 필수 입력값입니다. 키가 from_wall인지 확인해주세요.")
    private Float from_wall;
    @NotNull(message = "interval_value는 필수 입력값입니다. 키가 interval_value인지 확인해주세요.")
    private Float interval_value;
    @NotNull(message = "circle_rotate는 필수 입력값입니다. 키가 circle_rotate인지 확인해주세요.")
    private Float circle_rotate;
    @NotNull(message = "rect_rotate는 필수 입력값입니다. 키가 rect_rotate인지 확인해주세요.")
    private Float rect_rotate;
    @NotNull(message = "checked는 필수 입력값입니다. 키가 checked인지 확인해주세요.")
    private Boolean checked;
    @NotNull(message = "height_interval은 필수 입력값입니다. 키가 height_interval인지 확인해주세요.")
    private Float height_interval;
    @NotNull(message = "lower_height는 필수 입력값입니다. 키가 lower_height인지 확인해주세요.")
    private Float lower_height;
    @NotNull(message = "upper_hegight는 필수 입력값입니다. 키가 upper_height인지 확인해주세요.")
    private Float upper_height;
    @NotNull(message = "group_num는 필수 입력값입니다. 키가 group_num인지 확인해주세요.")
    private Integer group_num;

    private Boolean line_auto;
    private String line_direction;
    private Boolean circle_inward;
    private Float circle_start_angle;
    private Float circle_coeffs_x;
    private Float circle_coeffs_y;
    private Float circle_coeffs_radius;
    private Boolean rect_inward;
    private Float rectCoeffsPoint1X;
    private Float rectCoeffsPoint1Y;
    private Float rectCoeffsPoint2X;
    private Float rectCoeffsPoint2Y;
    private Float rectCoeffsPoint3X;
    private Float rectCoeffsPoint3Y;
    private Float rectCoeffsPoint4X;
    private Float rectCoeffsPoint4Y;
    private Float rect_coeffs_rot;
    private Boolean bottom_auto;
    private Boolean bottom_whole;
}
