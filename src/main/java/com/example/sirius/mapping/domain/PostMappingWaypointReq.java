package com.example.sirius.mapping.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMappingWaypointReq {
    @NotNull(message = "Filed Name이 seq 인지 확인하십시오.")
    private Integer seq;
    @NotNull(message = "Filed Name이 latitude 인지 확인하십시오.")
    private Double latitude;
    @NotNull(message = "Filed Name이 longitude 인지 확인하십시오.")
    private Double longitude;
    @NotNull(message = "Filed Name이 altitude 인지 확인하십시오.")
    private Double altitude;
    private String wait;
}
