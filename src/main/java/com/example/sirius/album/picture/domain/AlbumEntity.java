package com.example.sirius.album.picture.domain;


import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.plan.domain.MissionEntity;
import com.example.sirius.plan.domain.PropertyEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "albums")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regdate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionEntity missionEntity;

    @JsonManagedReference
    @JsonIgnore
    @OneToOne(mappedBy = "albumEntity")
    private AnalysisEntity analysisEntity;

    public static AlbumEntity from(PostAlbumReq postAlbumReq, MissionEntity missionEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        return AlbumEntity.builder()
                .missionEntity(missionEntity)
                .regdate(LocalDateTime.parse(postAlbumReq.getRegdate(), formatter))
                .build();
    }

    public static AlbumEntity from(String regdate, MissionEntity missionEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        return AlbumEntity.builder()
                .missionEntity(missionEntity)
                .regdate(LocalDateTime.parse(regdate, formatter))
                .build();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    public GetAlbumRes toDto() {
        GetAlbumRes getAlbumRes = new GetAlbumRes();
        getAlbumRes.setId(this.id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        getAlbumRes.setRegdate(this.regdate.format(formatter));
        getAlbumRes.setCreatedAt(this.createdAt.format(formatter));
        return getAlbumRes;
    }
}
