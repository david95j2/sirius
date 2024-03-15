package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetShapeRes {
    private Integer id;
    private Integer seq;
    private Integer groupNum;
    private String shape;
    private Float fromWall;
    private Float intervalValue;
    private Float circleRotate;
    private Float rectRotate;
    private Boolean checked;
    private Float heightInterval;
    private Float lowerHeight;
    private Float upperHeight;
    private PropertyDto propertyDto;
}
