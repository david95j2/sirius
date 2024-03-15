package com.example.sirius.album.analysis.domain;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SegmentationInfoModel {
    private Integer id;
    private Integer maskId;
    private Double crackWidth;
    private Double crackLength;
    private Double crackWidthPx;
    private Double crackLengthPx;
    private Double xMin;
    private Double yMin;
    private Double xMax;
    private Double yMax;
    private Double crossPointCloudX;
    private Double crossPointCloudY;
    private Double crossPointCloudZ;
    private Double distance;
}
