package com.example.sirius.album.picture.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetAlbumDetailRes {
    private Integer id;
    private String regdate;
    private Integer totalImageCount;
    private Integer totalCrackCount;
    private String maker;
    private String model;
    private Integer imageHeight;
    private Integer imageWidth;
}
