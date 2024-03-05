package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GetWaypointRes {
    private Integer id;
    private Integer seq;
    private Double pos_x;
    private Double pos_y;
    private Double pos_z;
    private Double yaw;
    private Boolean checked;
    private Boolean completed;
    private Integer group_num;
    private Integer pitch;
    private String gimbal_pitch_array;
    private Boolean camera_on;
}
