package com.example.sirius.album.analysis.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetSegmentationRes {
    private Integer id;
    private String drawFileName;
    private String maskFileName;
    private String jsonFileName;
}
