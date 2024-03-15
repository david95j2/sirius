package com.example.sirius.plan.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shapes")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShapeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private Integer seq;
    private String shape;
    @Column(name = "from_wall")
    private Float fromWall;
    @Column(name = "interval_value")
    private Float intervalValue;
    @Column(name = "circle_rotate")
    private Float circleRotate;
    @Column(name = "rect_rotate")
    private Float rectRotate;
    private Boolean checked;
    @Column(name = "height_interval")
    private Float heightInterval;
    @Column(name = "lower_height")
    private Float lowerHeight;
    @Column(name = "upper_height")
    private Float upperHegiht;
    @Column(name = "group_num")
    private Integer groupNum;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionEntity missionEntity;

    @JsonManagedReference
    @JsonIgnore
    @OneToOne(mappedBy = "shapeEntity",cascade = CascadeType.REMOVE)
    private PropertyEntity propertyEntity;

    public static ShapeEntity from(PostShapeAndPropertyReq postShapeAndPropertyReq, MissionEntity missionEntity) {
        return ShapeEntity.builder()
                .missionEntity(missionEntity)
                .shape(postShapeAndPropertyReq.getShape())
                .seq(postShapeAndPropertyReq.getSeq())
                .groupNum(postShapeAndPropertyReq.getGroupNum())
                .fromWall(postShapeAndPropertyReq.getFromWall())
                .intervalValue(postShapeAndPropertyReq.getIntervalValue())
                .circleRotate(postShapeAndPropertyReq.getCircleRotate())
                .rectRotate(postShapeAndPropertyReq.getRectRotate())
                .checked(postShapeAndPropertyReq.getChecked())
                .heightInterval(postShapeAndPropertyReq.getHeightInterval())
                .lowerHeight(postShapeAndPropertyReq.getLowerHeight())
                .upperHegiht(postShapeAndPropertyReq.getUpperHeight())
                .build();
    }

    public GetShapeRes toDto() {
        GetShapeRes getShapeRes = new GetShapeRes();
        getShapeRes.setId(this.id);
        getShapeRes.setSeq(this.seq);
        getShapeRes.setShape(this.shape);
        getShapeRes.setGroupNum(this.groupNum);
        getShapeRes.setFromWall(this.fromWall);
        getShapeRes.setIntervalValue(this.intervalValue);
        getShapeRes.setCircleRotate(this.circleRotate);
        getShapeRes.setRectRotate(this.rectRotate);
        getShapeRes.setChecked(this.checked);
        getShapeRes.setHeightInterval(this.heightInterval);
        getShapeRes.setUpperHeight(this.upperHegiht);
        getShapeRes.setLowerHeight(this.lowerHeight);

        if (this.shape.equals("Line")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toLineDto());
        }
        if (this.shape.equals("Circle")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toCircleDto());
        }
        if (this.shape.equals("Rectangle")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toRectDto());
        }
        if (this.shape.equals("Underside")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toBottomDto());
        }
        if (this.shape.equals("Abutment")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toAbutmentDto());
        }
        return getShapeRes;
    }
}
