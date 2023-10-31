package com.example.sirius.plan;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.mapping.domain.MappingEntity;
import com.example.sirius.mapping.domain.MappingWayPointEntity;
import com.example.sirius.mapping.domain.PostMappingWaypointReq;
import com.example.sirius.plan.domain.PostWaypointReq;
import com.example.sirius.plan.domain.ShapeEntity;
import com.example.sirius.plan.domain.WaypointEntity;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WaypointService {
    private ShapeRepository shapeRepository;
    private WaypointRepository waypointRepository;

    public BaseResponse getWaypoints(Integer shapeId) {
        return new BaseResponse(ErrorCode.SUCCESS, waypointRepository.findAllByShapeId(shapeId));
    }

    public BaseResponse getWaypointById(Integer waypointId, Integer shapeId) {
        return new BaseResponse(ErrorCode.SUCCESS, waypointRepository.findByIdAndShapeId(waypointId,shapeId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        ));
    }

    public BaseResponse postWaypoint(PostWaypointReq postWaypointReq, Integer shapeId) {
        ShapeEntity shapeEntity = shapeRepository.findById(shapeId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND));

        WaypointEntity waypointEntity = WaypointEntity.from(postWaypointReq, shapeEntity);
        Integer id = waypointRepository.save(waypointEntity).getId();

        return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(id)+"번 waypoint가 생성되었습니다.");
    }

    public BaseResponse deleteWayPoint(Integer waypoint_id,Integer shapeId) {
        WaypointEntity waypointEntity = waypointRepository.findByIdAndShapeId(waypoint_id,shapeId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        waypointRepository.delete(waypointEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(waypoint_id)+"번 Waypoint가 삭제되었습니다.");
    }
}
