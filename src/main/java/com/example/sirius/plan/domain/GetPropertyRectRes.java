package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPropertyRectRes implements PropertyDto {
    private Integer id;
    private Boolean rectInward;
    private Float rectCoeffsXmin;
    private Float rectCoeffsYmin;
    private Float rectCoeffsXmax;
    private Float rectCoeffsYmax;
    private Float rectCoeffsRot;
}
