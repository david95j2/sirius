package com.example.sirius.album.analysis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JsonBoxModel {
    private Double xmin;
    private Double ymin;
    private Double xmax;
    private Double ymax;
}
