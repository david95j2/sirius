package com.example.sirius.album.picture.domain;

import com.example.sirius.album.analysis.SegmentationRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pictures")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PictureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "file_path")
    private String filePath;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;
    @Column(name = "pos_x")
    private Float posX;
    @Column(name = "pos_y")
    private Float posY;
    @Column(name = "pos_z")
    private Float posZ;
    private Float roll;
    private Float pitch;
    private Float yaw;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "album_id")
    private AlbumEntity albumEntity;

    public static PictureEntity from(String filePath, String date, String time, Float posX, Float posY, Float posZ, double roll, double pitch, double yaw, AlbumEntity albumEntity) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        LocalDate parsedDate = LocalDate.parse(date, dateFormatter);
        LocalTime parsedTime = LocalTime.parse(time, timeFormatter);

        return PictureEntity.builder()
                .albumEntity(albumEntity)
                .filePath(filePath)
                .date(parsedDate)
                .time(parsedTime)
                .posX(posX)
                .posY(posY)
                .posZ(posZ)
                .roll((float) roll)
                .pitch((float) pitch)
                .yaw((float) yaw)
                .build();
    }

    public GetPictureRes toDto() {
        GetPictureRes getPictureRes = new GetPictureRes();
        String fileName = Paths.get(this.filePath).getFileName().toString();
        getPictureRes.setId(this.id);
        getPictureRes.setFileName(fileName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime combinedDateTime = LocalDateTime.of(this.date, this.time);
        String formattedDate = combinedDateTime.format(formatter);
        getPictureRes.setRegdate(formattedDate);
        getPictureRes.setPosX(this.posX);
        getPictureRes.setPosY(this.posY);
        getPictureRes.setPosZ(this.posZ);
        getPictureRes.setRoll(this.roll);
        getPictureRes.setPitch(this.pitch);
        getPictureRes.setYaw(this.yaw);
        getPictureRes.setCrack(false);
        return getPictureRes;
    }

}
