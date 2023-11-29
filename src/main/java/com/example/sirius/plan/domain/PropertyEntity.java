package com.example.sirius.plan.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "properties")
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PropertyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "line_auto")
    private Boolean lineAuto;
    @Column(name = "line_direction")
    private String lineDirection;
    @Column(name = "circle_inward")
    private Boolean circleInward;
    @Column(name = "circle_start_angle")
    private Float circleStartAngle;
    @Column(name = "circle_coeffs_x")
    private Float circleCoeffsX;
    @Column(name = "circle_coeffs_y")
    private Float circleCoeffsY;
    @Column(name = "circle_coeffs_radius")
    private Float circleCoeffsRadius;
    @Column(name = "rect_inward")
    private Boolean rectInward;
    @Column(name = "rect_coeffs_point1_x")
    private Float rectCoeffsPoint1X;
    @Column(name = "rect_coeffs_point1_y")
    private Float rectCoeffsPoint1Y;
    @Column(name = "rect_coeffs_point2_x")
    private Float rectCoeffsPoint2X;
    @Column(name = "rect_coeffs_point2_y")
    private Float rectCoeffsPoint2Y;
    @Column(name = "rect_coeffs_point3_x")
    private Float rectCoeffsPoint3X;
    @Column(name = "rect_coeffs_point3_y")
    private Float rectCoeffsPoint3Y;
    @Column(name = "rect_coeffs_point4_x")
    private Float rectCoeffsPoint4X;
    @Column(name = "rect_coeffs_point4_y")
    private Float rectCoeffsPoint4Y;
    @Column(name = "rect_coeffs_rot")
    private Float rectCoeffsRot;
    @Column(name = "bottom_auto")
    private Boolean bottomAuto;
    @Column(name = "bottom_whole")
    private Boolean bottomWhole;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "shape_id")
    private ShapeEntity shapeEntity;

    public static PropertyEntity from(PostPropertyReq postPropertyReq, ShapeEntity shapeEntity) {
        return PropertyEntity.builder()
                .shapeEntity(shapeEntity)
                .lineAuto(postPropertyReq.getLineAuto())
                .lineDirection(postPropertyReq.getLineDirection())
                .circleInward(postPropertyReq.getCircleInward())
                .circleStartAngle(postPropertyReq.getCircleStartAngle())
                .circleCoeffsX(postPropertyReq.getCircleCoeffsX())
                .circleCoeffsY(postPropertyReq.getCircleCoeffsY())
                .circleCoeffsRadius(postPropertyReq.getCircleCoeffsRadius())
                .rectInward(postPropertyReq.getRectInward())
                .rectCoeffsPoint1X(postPropertyReq.getRectCoeffsPoint1X())
                .rectCoeffsPoint1Y(postPropertyReq.getRectCoeffsPoint1Y())
                .rectCoeffsPoint2X(postPropertyReq.getRectCoeffsPoint2X())
                .rectCoeffsPoint2Y(postPropertyReq.getRectCoeffsPoint2Y())
                .rectCoeffsPoint3X(postPropertyReq.getRectCoeffsPoint3X())
                .rectCoeffsPoint3Y(postPropertyReq.getRectCoeffsPoint3Y())
                .rectCoeffsPoint4X(postPropertyReq.getRectCoeffsPoint4X())
                .rectCoeffsPoint4Y(postPropertyReq.getRectCoeffsPoint4Y())
                .rectCoeffsRot(postPropertyReq.getRectCoeffsRot())
                .bottomAuto(postPropertyReq.getBottomAuto())
                .bottomWhole(postPropertyReq.getBottomWhole())
                .build();
    }

    public GetPropertyLineRes toLineDto() {
        GetPropertyLineRes getPropertyLineRes = new GetPropertyLineRes();
        getPropertyLineRes.setId(this.id);
        getPropertyLineRes.setLineAuto(this.lineAuto);
        getPropertyLineRes.setLineDirection(this.lineDirection);
        return getPropertyLineRes;
    }

    public GetPropertyCircleRes toCircleDto() {
        GetPropertyCircleRes getPropertyCircleRes = new GetPropertyCircleRes();
        getPropertyCircleRes.setId(this.id);
        getPropertyCircleRes.setCircleInward(this.circleInward);
        getPropertyCircleRes.setCircleStartAngle(this.circleStartAngle);
        getPropertyCircleRes.setCircleCoeffsX(this.circleCoeffsX);
        getPropertyCircleRes.setCircleCoeffsY(this.circleCoeffsY);
        getPropertyCircleRes.setCircleCoeffsRadius(this.circleCoeffsRadius);
        return getPropertyCircleRes;
    }

    public GetPropertyRectRes toRectDto() {
        GetPropertyRectRes getPropertyRectRes = new GetPropertyRectRes();
        getPropertyRectRes.setId(this.id);
        getPropertyRectRes.setRectInward(this.rectInward);
        getPropertyRectRes.setRectCoeffsPoint1X(this.rectCoeffsPoint1X);
        getPropertyRectRes.setRectCoeffsPoint1Y(this.rectCoeffsPoint1Y);
        getPropertyRectRes.setRectCoeffsPoint2X(this.rectCoeffsPoint2X);
        getPropertyRectRes.setRectCoeffsPoint2Y(this.rectCoeffsPoint2Y);
        getPropertyRectRes.setRectCoeffsPoint3X(this.rectCoeffsPoint3X);
        getPropertyRectRes.setRectCoeffsPoint3Y(this.rectCoeffsPoint3Y);
        getPropertyRectRes.setRectCoeffsPoint4X(this.rectCoeffsPoint4X);
        getPropertyRectRes.setRectCoeffsPoint4Y(this.rectCoeffsPoint4Y);
        getPropertyRectRes.setRectCoeffsRot(this.rectCoeffsRot);
        return getPropertyRectRes;
    }

    public GetPropertyBottomRes toBottomDto() {
        GetPropertyBottomRes getPropertyBottomRes = new GetPropertyBottomRes();
        getPropertyBottomRes.setId(this.id);
        getPropertyBottomRes.setBottomAuto(this.bottomAuto);
        getPropertyBottomRes.setBottomWhole(this.bottomWhole);
        return getPropertyBottomRes;
    }

    public GetPropertyAbutmentRes toAbutmentDto() {
        GetPropertyAbutmentRes getPropertyAbutmentRes = new GetPropertyAbutmentRes();
        getPropertyAbutmentRes.setId(this.id);
        return getPropertyAbutmentRes;
    }
}
