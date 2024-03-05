package com.example.sirius.plan.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPropertyBottomRes implements PropertyDto{
    private Integer id;
    private Boolean bottomAuto;
    private String bottomWhole;
}
