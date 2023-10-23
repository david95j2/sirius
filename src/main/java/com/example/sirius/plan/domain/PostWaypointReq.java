package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
}
