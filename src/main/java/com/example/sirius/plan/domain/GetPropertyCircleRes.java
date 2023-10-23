package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPropertyCircleRes implements PropertyDto {
    private Integer id;
    private Boolean circleInward;
    private Float circleStartAngle;
    private Float circleCoeffsX;
    private Float circleCoeffsY;
    private Float circleCoeffsRadius;
}
