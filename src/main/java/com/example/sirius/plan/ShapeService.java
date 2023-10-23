package com.example.sirius.plan;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.plan.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShapeService {
    private MissionRepository missionRepository;
    private ShapeRepository shapeRepository;
    private PropertyRepository propertyRepository;

    public BaseResponse getShapes(Integer missionId) {
        List<ShapeEntity> results = shapeRepository.findByMissionId(missionId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(ShapeEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getShapeById(Integer shapeId, Integer missionId) {
        ShapeEntity shapeEntity = shapeRepository.findByIdAndMissionId(shapeId,missionId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        return new BaseResponse(ErrorCode.SUCCESS,shapeEntity.toDto());
    }

    public BaseResponse postShape(PostShapeAndPropertyReq postShapeAndPropertyReq, Integer missionId) {
        MissionEntity missionEntity = missionRepository.findById(missionId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        ShapeEntity shapeEntity = ShapeEntity.from(postShapeAndPropertyReq, missionEntity);
        Integer createdNum = shapeRepository.save(shapeEntity).getId();

        PropertyEntity propertyEntity = createPropertyFromRequest(postShapeAndPropertyReq, shapeEntity);
        propertyRepository.save(propertyEntity);

        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(createdNum) + "번 도형이 생성되었습니다.");
    }
    @Transactional
    public BaseResponse patchShape(PatchShapeAndPropertyReq patchShapeAndPropertyReq, Integer shapeId, Integer missionId) {
        ShapeEntity shapeEntity = shapeRepository.findByIdAndMissionId(shapeId, missionId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        PropertyEntity propertyEntity = shapeEntity.getPropertyEntity();
        if (patchShapeAndPropertyReq.getShape() != null) {
            shapeEntity.setShape(patchShapeAndPropertyReq.getShape());
        }
        if (patchShapeAndPropertyReq.getFrom_wall() != null) {
            shapeEntity.setFromWall(patchShapeAndPropertyReq.getFrom_wall());
        }
        if (patchShapeAndPropertyReq.getInterval_value() != null) {
            shapeEntity.setIntervalValue(patchShapeAndPropertyReq.getInterval_value());
        }
        if (patchShapeAndPropertyReq.getCircle_rotate() != null) {
            shapeEntity.setCircleRotate(patchShapeAndPropertyReq.getCircle_rotate());
        }
        if (patchShapeAndPropertyReq.getRect_rotate() != null) {
            shapeEntity.setRectRotate(patchShapeAndPropertyReq.getRect_rotate());
        }
        if (patchShapeAndPropertyReq.getChecked() != null) {
            shapeEntity.setChecked(patchShapeAndPropertyReq.getChecked());
        }
        if (patchShapeAndPropertyReq.getHeight_interval() != null) {
            shapeEntity.setHeightInterval(patchShapeAndPropertyReq.getHeight_interval());
        }
        if (patchShapeAndPropertyReq.getLower_height() != null) {
            shapeEntity.setLowerHeight(patchShapeAndPropertyReq.getLower_height());
        }
        if (patchShapeAndPropertyReq.getUpper_height() != null) {
            shapeEntity.setUpperHegiht(patchShapeAndPropertyReq.getUpper_height());
        }
        Integer modifiedNum = shapeRepository.save(shapeEntity).getId();

        // property 값도 있으면 property 값 변경
        if (patchShapeAndPropertyReq.getLine_auto() != null) {
            propertyEntity.setLineAuto((patchShapeAndPropertyReq.getLine_auto()));
        }
        if (patchShapeAndPropertyReq.getLine_direction() != null) {
            propertyEntity.setLineDirection(patchShapeAndPropertyReq.getLine_direction());
        }
        if (patchShapeAndPropertyReq.getCircle_inward() != null) {
            propertyEntity.setCircleInward(patchShapeAndPropertyReq.getCircle_inward());
        }
        if (patchShapeAndPropertyReq.getCircle_start_angle() != null) {
            propertyEntity.setCircleStartAngle(patchShapeAndPropertyReq.getCircle_start_angle());
        }
        if (patchShapeAndPropertyReq.getCircle_coeffs_x() != null) {
            propertyEntity.setCircleCoeffsX(patchShapeAndPropertyReq.getCircle_coeffs_x());
        }
        if (patchShapeAndPropertyReq.getCircle_coeffs_y() != null) {
            propertyEntity.setCircleCoeffsY(patchShapeAndPropertyReq.getCircle_coeffs_y());
        }
        if (patchShapeAndPropertyReq.getCircle_coeffs_radius() != null) {
            propertyEntity.setCircleCoeffsRadius(patchShapeAndPropertyReq.getCircle_coeffs_radius());
        }
        if (patchShapeAndPropertyReq.getRect_inward() != null) {
            propertyEntity.setRectInward(patchShapeAndPropertyReq.getRect_inward());
        }
        if (patchShapeAndPropertyReq.getRect_coeffs_xmin() != null) {
            propertyEntity.setRectCoeffsXmin(patchShapeAndPropertyReq.getRect_coeffs_xmin());
        }
        if (patchShapeAndPropertyReq.getRect_coeffs_ymin() != null) {
            propertyEntity.setRectCoeffsYmin(patchShapeAndPropertyReq.getRect_coeffs_ymin());
        }
        if (patchShapeAndPropertyReq.getRect_coeffs_xmax() != null) {
            propertyEntity.setRectCoeffsXmax(patchShapeAndPropertyReq.getRect_coeffs_xmax());
        }
        if (patchShapeAndPropertyReq.getRect_coeffs_ymax() != null) {
            propertyEntity.setRectCoeffsYmax(patchShapeAndPropertyReq.getRect_coeffs_ymax());
        }
        if (patchShapeAndPropertyReq.getBottom_auto() != null) {
            propertyEntity.setBottomAuto(patchShapeAndPropertyReq.getBottom_auto());
        }
        if (patchShapeAndPropertyReq.getBottom_whole() != null) {
            propertyEntity.setBottomWhole(patchShapeAndPropertyReq.getBottom_whole());
        }
        propertyRepository.save(propertyEntity);

        return new BaseResponse(ErrorCode.ACCEPTED, Integer.valueOf(modifiedNum)+"번 도형의 값이 변경되었습니다.");
    }

    @Transactional
    public BaseResponse deleteShape(Integer shapeId, Integer missionId) {
        PropertyEntity propertyEntity = (PropertyEntity) getShapeById(shapeId, missionId).getResult();
        shapeRepository.deleteById(shapeId);
        propertyRepository.delete(propertyEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(shapeId)+"번 도형이 삭제되었습니다.");
    }

    private PropertyEntity createPropertyFromRequest(PostShapeAndPropertyReq request, ShapeEntity shapeEntity) throws AppException {
        PropertyEntity propertyEntity = PropertyEntity.builder()
                        .shapeEntity(shapeEntity).build();

        switch (request.getShape().toLowerCase()) {
            case "rectangle":
                checkNotNull(request.getRect_inward(), "rect_inward");
                checkNotNull(request.getRect_coeffs_xmin(), "rect_coeffs_xmin");
                checkNotNull(request.getRect_coeffs_ymin(), "rect_coeffs_ymin");
                checkNotNull(request.getRect_coeffs_xmax(), "rect_coeffs_xmax");
                checkNotNull(request.getRect_coeffs_ymax(),"rect_coeffs_ymax");
                checkNotNull(request.getRect_coeffs_rot(), "rect_coeffs_rot");
                // Assign the values to the entity
                propertyEntity.setRectInward(request.getRect_inward());
                propertyEntity.setRectCoeffsXmin(request.getRect_coeffs_xmin());
                propertyEntity.setRectCoeffsYmin(request.getRect_coeffs_ymin());
                propertyEntity.setRectCoeffsXmax(request.getRect_coeffs_xmax());
                propertyEntity.setRectCoeffsYmax(request.getRect_coeffs_ymax());
                propertyEntity.setRectCoeffsRot(request.getRect_coeffs_rot());
                break;
            case "line":
                checkNotNull(request.getLine_auto(), "line_auto");
                checkNotNull(request.getLine_direction(), "line_direction");
                propertyEntity.setLineAuto(request.getLine_auto());
                propertyEntity.setLineDirection(request.getLine_direction());
                break;
            case "circle":
                checkNotNull(request.getCircle_inward(), "circle_inward");
                checkNotNull(request.getCircle_start_angle(), "circle_start_angle");
                checkNotNull(request.getCircle_coeffs_x(), "circle_coeffs_x");
                checkNotNull(request.getCircle_coeffs_y(), "circle_coeffs_y");
                checkNotNull(request.getCircle_coeffs_radius(), "circle_coeffs_radius");
                propertyEntity.setCircleInward(request.getCircle_inward());
                propertyEntity.setCircleStartAngle(request.getCircle_start_angle());
                propertyEntity.setCircleCoeffsX(request.getCircle_coeffs_x());
                propertyEntity.setCircleCoeffsY(request.getCircle_coeffs_y());
                propertyEntity.setCircleCoeffsRadius(request.getCircle_coeffs_radius());
                break;
            case "bottom":
                checkNotNull(request.getBottom_auto(), "bottom_auto");
                checkNotNull(request.getBottom_whole(), "bottom_whole");
                propertyEntity.setBottomAuto(request.getBottom_auto());
                propertyEntity.setBottomWhole(request.getBottom_whole());
                break;
            case "waypoints":
                break;
            default:
                throw new AppException(ErrorCode.INVALID_INPUT);
        }

        return propertyEntity;
    }

    private void checkNotNull(Object value, String fieldName) throws AppException {
        if (value == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, fieldName + " is missing or null.");
        }
    }
}
