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
    private String circleInward;
    private Float circleStartAngle;
    private Float circleCoeffsX;
    private Float circleCoeffsY;
    private Float circleCoeffsRadius;
    private String rectInward;
    private Float rectCoeffsPoint1X;
    private Float rectCoeffsPoint1Y;
    private Float rectCoeffsPoint2X;
    private Float rectCoeffsPoint2Y;
    private Float rectCoeffsPoint3X;
    private Float rectCoeffsPoint3Y;
    private Float rectCoeffsPoint4X;
    private Float rectCoeffsPoint4Y;
    private Float rectCoeffsRot;
    private Boolean bottomAuto;
    private String bottomWhole;
}
