package com.example.sirius.facility.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "thumbnails")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ThumbnailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "thumbnail_path")
    private String thumbnailPath;
    @Column(name = "thumbnail_regdate")
    private LocalDateTime thumbnailRegdate;
    @Column(name = "thumbnail_last_regdate")
    private String thumbnailLastRegdate;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private FacilityEntity facilityEntity;

    public static ThumbnailEntity from(PostThumbnails postThumbnails, FacilityEntity facilityEntity) {
        DateTimeFormatter convertDate = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return ThumbnailEntity.builder()
                .facilityEntity(facilityEntity)
                .thumbnailPath(postThumbnails.getFile_path())
                .thumbnailRegdate(LocalDateTime.parse(postThumbnails.getRegdate(),convertDate))
                .build();
    }
}
