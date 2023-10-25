package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPropertyRectRes implements PropertyDto {
    private Integer id;
    private Boolean rectInward;
    private Float rectCoeffsPoint1X;
    private Float rectCoeffsPoint1Y;
    private Float rectCoeffsPoint2X;
    private Float rectCoeffsPoint2Y;
    private Float rectCoeffsPoint3X;
    private Float rectCoeffsPoint3Y;
    private Float rectCoeffsPoint4X;
    private Float rectCoeffsPoint4Y;
    private Float rectCoeffsRot;
}
