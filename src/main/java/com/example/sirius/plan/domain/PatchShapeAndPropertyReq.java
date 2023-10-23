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
    private Boolean circle_inward;
    private Float circle_start_angle;
    private Float circle_coeffs_x;
    private Float circle_coeffs_y;
    private Float circle_coeffs_radius;
    private Boolean rect_inward;
    private Float rect_coeffs_xmin;
    private Float rect_coeffs_ymin;
    private Float rect_coeffs_xmax;
    private Float rect_coeffs_ymax;
    private Float rect_coeffs_rot;
    private Boolean bottom_auto;
    private Boolean bottom_whole;
}
