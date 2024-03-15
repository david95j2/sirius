package com.example.sirius.plan.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostShapeAndPropertyReq {
    @NotBlank(message = "도형은 필수 입력값입니다. 키가 shape인지 확인해주세요.")
    private String shape;
    @NotNull(message = "seq은 필수 입력값입니다. 키가 seq인지 확인해주세요.")
    private Integer seq;
    @NotNull(message = "fromWall은 필수 입력값입니다. 키가 fromWall인지 확인해주세요.")
    private Float fromWall;
    @NotNull(message = "intervalValue는 필수 입력값입니다. 키가 intervalValue인지 확인해주세요.")
    private Float intervalValue;
    @NotNull(message = "circleRotate는 필수 입력값입니다. 키가 circleRotate인지 확인해주세요.")
    private Float circleRotate;
    @NotNull(message = "rectRotate는 필수 입력값입니다. 키가 rectRotate인지 확인해주세요.")
    private Float rectRotate;
    @NotNull(message = "checked는 필수 입력값입니다. 키가 checked인지 확인해주세요.")
    private Boolean checked;
    @NotNull(message = "heightInterval은 필수 입력값입니다. 키가 heightInterval인지 확인해주세요.")
    private Float heightInterval;
    @NotNull(message = "lowerHeight는 필수 입력값입니다. 키가 lowerHeight인지 확인해주세요.")
    private Float lowerHeight;
    @NotNull(message = "upperHegight는 필수 입력값입니다. 키가 upperHeight인지 확인해주세요.")
    private Float upperHeight;
    @NotNull(message = "groupNum는 필수 입력값입니다. 키가 groupNum인지 확인해주세요.")
    private Integer groupNum;

    private Boolean lineAuto;
    private String lineDirection;
    private String circleInward;
    private Float circleStartAngle;
    private Float circleCoeffsX;
    private Float circleCoeffsY;
    private Float circleCoeffsRadius;
    private String rectInward;
    private Float rectCoeffsPoint1X;
    private Float rectCoeffsPoint1Y;
    private Float rectCoeffsPoint2X;
    private Float rectCoeffsPoint2Y;
    private Float rectCoeffsPoint3X;
    private Float rectCoeffsPoint3Y;
    private Float rectCoeffsPoint4X;
    private Float rectCoeffsPoint4Y;
    private Float rectCoeffsRot;
    private Boolean bottomAuto;
    private String bottomWhole;
}
