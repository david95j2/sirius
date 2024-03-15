package com.example.sirius.facility.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PatchFacilityReq {
    private String name;
    private Float latitude;
    private Float longitude;
    private String description;
}
