package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchShapeAndPropertyReq {
    /* ShapeEntity 변경을 위한 값들 */
    private String shape;
    private Integer seq;
    private Integer group_num;
    private Float from_wall;
    private Float interval_value;
    private Float circle_rotate;
    private Float rect_rotate;
    private Boolean checked;
    private Float height_interval;
    private Float lower_height;
    private Float upper_height;

    /* ShapeEntity와 1:1 관계인 PropertyEntity 변경을 위한 값들 */
    private Boolean line_auto;
    private String line_direction;
    private String circle_inward;
    private Float circle_start_angle;
    private Float circle_coeffs_x;
    private Float circle_coeffs_y;
    private Float circle_coeffs_radius;
    private String rect_inward;
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
    private String bottom_whole;
}
