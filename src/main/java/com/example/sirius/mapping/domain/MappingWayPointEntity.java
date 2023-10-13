package com.example.sirius.mapping.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mapping_waypoints")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MappingWayPointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private Integer seq;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private String wait;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "mapping_id")
    private MappingEntity mappingEntity;

    public static MappingWayPointEntity from(PostMappingWaypointReq postMappingWayPointReq, MappingEntity mappingEntity) {
        return MappingWayPointEntity.builder()
                .seq(postMappingWayPointReq.getSeq())
                .latitude(postMappingWayPointReq.getLatitude())
                .longitude(postMappingWayPointReq.getLongitude())
                .altitude(postMappingWayPointReq.getAltitude())
                .wait(postMappingWayPointReq.getWait())
                .mappingEntity(mappingEntity)
                .build();
    }
}
