package com.example.sirius.plan.domain;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostPropertyReq {
    private Boolean lineAuto;
    private String lineDirection;
    private Boolean circleInward;
    private Float circleStartAngle;
    private Float circleCoeffsX;
    private Float circleCoeffsY;
    private Float circleCoeffsRadius;
    private Boolean rectInward;
    private Float rectCoeffsXmin;
    private Float rectCoeffsYmin;
    private Float rectCoeffsXmax;
    private Float rectCoeffsYmax;
    private Float rectCoeffsRot;
    private Boolean bottomAuto;
    private Boolean bottomWhole;
}
