package com.example.sirius.album.analysis.domain;

import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.plan.domain.ShapeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "analyses")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regdate;
    private Integer status;
    @Column(name = "ai_type")
    private String aiType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @PrePersist
    public void prePersist() {
        this.regdate = LocalDateTime.now();
    }


    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "album_id")
    private AlbumEntity albumEntity;


    public static AnalysisEntity from(PostAnalysisReq postAnalysisReq, AlbumEntity albumEntity) {
        return AnalysisEntity.builder()
                .albumEntity(albumEntity)
                .status(0)
                .aiType(postAnalysisReq.getAi_type())
                .build();
    }

    public static AnalysisEntity from(String aiType, AlbumEntity albumEntity) {
        return AnalysisEntity.builder()
                .albumEntity(albumEntity)
                .status(0)
                .aiType(aiType)
                .build();
    }

    public GetAnalysisRes toDto() {
        GetAnalysisRes getAnalysisRes = new GetAnalysisRes();
        getAnalysisRes.setId(this.id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        getAnalysisRes.setRegdate(this.regdate.format(formatter));
        if (this.getStatus() == 1) {
            getAnalysisRes.setStatus("완료");
        } else {
            getAnalysisRes.setStatus("진행중");
        }
        getAnalysisRes.setAiType(this.aiType);
        return getAnalysisRes;
    }
}
