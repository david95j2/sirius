package com.example.sirius.plan.domain;

import com.example.sirius.mapping.domain.MappingEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Column(name = "group_num")
    private Integer groupNum;
    private Integer pitch;
    @Column(name = "gimbal_pitch_array")
    private String gimbalPitchArray;
    @Column(name = "camera_on")
    private Boolean cameraOn;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "shape_id")
    private ShapeEntity shapeEntity;

    public static WaypointEntity from(PostWaypointReq postWaypointReq, ShapeEntity shapeEntity) {
        Integer pitch;
        if (postWaypointReq.getPitch() == null) {
            pitch = null;
        } else {
            pitch = postWaypointReq.getPitch();
        }

        return WaypointEntity.builder()
                .shapeEntity(shapeEntity)
                .groupNum(postWaypointReq.getGroup_num())
                .seq(postWaypointReq.getSeq())
                .posX(postWaypointReq.getPos_x())
                .posY(postWaypointReq.getPos_y())
                .posZ(postWaypointReq.getPos_z())
                .yaw(postWaypointReq.getYaw())
                .checked(postWaypointReq.getChecked())
                .completed(postWaypointReq.getCompleted())
                .pitch(pitch)
                .gimbalPitchArray(postWaypointReq.getGimbal_pitch_array_string())
                .cameraOn(postWaypointReq.getCamera_on())
                .build();
    }

    public GetWaypointRes toDto() {
        GetWaypointRes getWaypointRes = new GetWaypointRes();
        getWaypointRes.setId(this.id);
        getWaypointRes.setSeq(this.seq);
        getWaypointRes.setPos_x(this.posX);
        getWaypointRes.setPos_y(this.posY);
        getWaypointRes.setPos_z(this.posZ);
        getWaypointRes.setYaw(this.yaw);
        getWaypointRes.setChecked(this.checked);
        getWaypointRes.setCompleted(this.completed);
        getWaypointRes.setGroup_num(this.groupNum);
        getWaypointRes.setPitch(this.pitch);

        String temp_gimbalPitchArray = this.gimbalPitchArray.replaceAll("\\[|\\]", "");
        getWaypointRes.setGimbal_pitch_array(Arrays.stream(temp_gimbalPitchArray.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList()));

        getWaypointRes.setCamera_on(this.cameraOn);
        return getWaypointRes;
    }
}
