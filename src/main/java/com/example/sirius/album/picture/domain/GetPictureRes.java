package com.example.sirius.album.picture.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class GetPictureRes {
    private Integer id;
    private String fileName;
    private String regdate;
    private Float posX;
    private Float posY;
    private Float posZ;
    private Float roll;
    private Float pitch;
    private Float yaw;
}
