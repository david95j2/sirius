package com.example.sirius.plan.domain;

import com.example.sirius.mapping.domain.MappingEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waypoints")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaypointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private Integer seq;
    @Column(name = "pos_x")
    private Double posX;
    @Column(name = "pos_y")
    private Double posY;
    @Column(name = "pos_z")
    private Double posZ;
    private Double yaw;
    private Boolean checked;
    private Boolean completed;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "shape_id")
    private ShapeEntity shapeEntity;

    public static WaypointEntity from(PostWaypointReq postWaypointReq, ShapeEntity shapeEntity) {
        return WaypointEntity.builder()
                .shapeEntity(shapeEntity)
                .seq(postWaypointReq.getSeq())
                .posX(postWaypointReq.getPos_x())
                .posY(postWaypointReq.getPos_y())
                .posZ(postWaypointReq.getPos_z())
                .yaw(postWaypointReq.getYaw())
                .checked(postWaypointReq.getChecked())
                .completed(postWaypointReq.getCompleted())
                .build();
    }
}
