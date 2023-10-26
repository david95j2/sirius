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
                .groupNum(postShapeAndPropertyReq.getGroup_num())
                .fromWall(postShapeAndPropertyReq.getFrom_wall())
                .intervalValue(postShapeAndPropertyReq.getInterval_value())
                .circleRotate(postShapeAndPropertyReq.getCircle_rotate())
                .rectRotate(postShapeAndPropertyReq.getRect_rotate())
                .checked(postShapeAndPropertyReq.getChecked())
                .heightInterval(postShapeAndPropertyReq.getHeight_interval())
                .lowerHeight(postShapeAndPropertyReq.getLower_height())
                .upperHegiht(postShapeAndPropertyReq.getUpper_height())
                .build();
    }

    public GetShapeRes toDto() {
        GetShapeRes getShapeRes = new GetShapeRes();
        getShapeRes.setId(this.id);
        getShapeRes.setSeq(this.seq);
        getShapeRes.setShape(this.shape);
        getShapeRes.setGroup_num(this.groupNum);
        getShapeRes.setFrom_wall(this.fromWall);
        getShapeRes.setInterval_value(this.intervalValue);
        getShapeRes.setCircle_rotate(this.circleRotate);
        getShapeRes.setRect_rotate(this.rectRotate);
        getShapeRes.setChecked(this.checked);
        getShapeRes.setHeight_interval(this.heightInterval);
        getShapeRes.setUpper_height(this.upperHegiht);
        getShapeRes.setLower_height(this.lowerHeight);

        if (this.shape.equals("line")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toLineDto());
        }
        if (this.shape.equals("circle")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toCircleDto());
        }
        if (this.shape.equals("rectangle")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toRectDto());
        }
        if (this.shape.equals("bottom")) {
            getShapeRes.setPropertyDto(this.propertyEntity.toBottomDto());
        }
        return getShapeRes;
    }
}
