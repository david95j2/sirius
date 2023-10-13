package com.example.sirius.map.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class PostMapReq {
    private String file_path;
    private Integer map_count;
    private Float map_area;
    private LocalDate date;
    private LocalTime time;
}
