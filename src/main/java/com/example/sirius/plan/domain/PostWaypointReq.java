package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;

@Getter @Setter
public class PostWaypointReq {
    @NotNull(message = "Filed Name이 seq 인지 확인하십시오.")
    private Integer seq;
    @NotNull(message = "Filed Name이 pos_x 인지 확인하십시오.")
    private Double pos_x;
    @NotNull(message = "Filed Name이 pos_y 인지 확인하십시오.")
    private Double pos_y;
    @NotNull(message = "Filed Name이 pos_z 인지 확인하십시오.")
    private Double pos_z;
    @NotNull(message = "Filed Name이 yaw 인지 확인하십시오.")
    private Double yaw;
    @NotNull(message = "Filed Name이 checked 인지 확인하십시오.")
    private Boolean checked;
    @NotNull(message = "Filed Name이 completed 인지 확인하십시오.")
    private Boolean completed;
    @NotNull(message = "Filed Name이 group_num 인지 확인하십시오.")
    private Integer group_num;
    private Integer pitch;
    @NotNull(message = "Filed Name이 gimbal_pitch_array 인지 확인하십시오.")
    private List<Integer> gimbal_pitch_array;
    private Boolean camera_on;
    private String gimbal_pitch_array_string;
}
