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
    @Column(name = "rect_coeffs_xmin")
    private Float rectCoeffsXmin;
    @Column(name = "rect_coeffs_ymin")
    private Float rectCoeffsYmin;
    @Column(name = "rect_coeffs_xmax")
    private Float rectCoeffsXmax;
    @Column(name = "rect_coeffs_ymax")
    private Float rectCoeffsYmax;
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
                .rectCoeffsXmin(postPropertyReq.getRectCoeffsXmin())
                .rectCoeffsYmin(postPropertyReq.getRectCoeffsYmin())
                .rectCoeffsXmax(postPropertyReq.getRectCoeffsXmax())
                .rectCoeffsYmax(postPropertyReq.getRectCoeffsYmax())
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
        getPropertyRectRes.setRectCoeffsXmin(this.rectCoeffsXmin);
        getPropertyRectRes.setRectCoeffsYmin(this.rectCoeffsYmin);
        getPropertyRectRes.setRectCoeffsXmax(this.rectCoeffsXmax);
        getPropertyRectRes.setRectCoeffsYmax(this.rectCoeffsYmax);
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
}
