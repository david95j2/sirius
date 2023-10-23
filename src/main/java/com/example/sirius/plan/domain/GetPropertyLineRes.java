package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPropertyLineRes implements PropertyDto {
    private Integer id;
    private Boolean lineAuto;
    private String lineDirection;
}
