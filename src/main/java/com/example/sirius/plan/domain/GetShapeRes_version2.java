package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetShapeRes_version2 {
    private Integer id;
    private Integer seq;
    private Integer group_num;
    private String shape;
    private Float from_wall;
    private Float interval_value;
    private Float circle_rotate;
    private Float rect_rotate;
    private Boolean checked;
    private Float height_interval;
    private Float lower_height;
    private Float upper_height;
    private PropertyDto propertyDto;
}
