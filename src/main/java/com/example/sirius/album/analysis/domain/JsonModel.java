package com.example.sirius.album.analysis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class JsonModel {
    private Integer mask_id;
    private Double crack_width;
    private Double crack_length;
    private JsonBoxModel box;
    private ArrayList<ArrayList<Integer>> points;
}
