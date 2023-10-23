package com.example.sirius.album.analysis.domain;

import com.example.sirius.plan.domain.MissionEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.nio.file.Paths;

@Entity
@Table(name = "segmentations")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SegmentationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "draw_file_path")
    private String drawFilePath;
    @Column(name = "mask_file_path")
    private String maskFilePath;
    @Column(name = "json_file_path")
    private String jsonFilePath;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "analysis_id")
    private AnalysisEntity analysisEntity;

    public GetSegmentationRes toDto() {
        GetSegmentationRes getSegmentationRes = new GetSegmentationRes();
        getSegmentationRes.setId(this.id);
        getSegmentationRes.setDrawFileName(Paths.get(this.drawFilePath).getFileName().toString());
        getSegmentationRes.setMaskFileName(Paths.get(this.maskFilePath).getFileName().toString());
        getSegmentationRes.setJsonFileName(Paths.get(this.jsonFilePath).getFileName().toString());
        return getSegmentationRes;
    }
}
