package com.example.sirius.plan;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.mapping.domain.MappingEntity;
import com.example.sirius.mapping.domain.MappingWayPointEntity;
import com.example.sirius.mapping.domain.PostMappingWaypointReq;
import com.example.sirius.plan.domain.GetWaypointRes;
import com.example.sirius.plan.domain.PostWaypointReq;
import com.example.sirius.plan.domain.ShapeEntity;
import com.example.sirius.plan.domain.WaypointEntity;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WaypointService {
    private ShapeRepository shapeRepository;
    private WaypointRepository waypointRepository;

    public BaseResponse getWaypoints(Integer shapeId) {
        List<WaypointEntity> results = waypointRepository.findAllByShapeId(shapeId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(WaypointEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getWaypointById(Integer waypointId, Integer shapeId) {
        return new BaseResponse(ErrorCode.SUCCESS, waypointRepository.findByIdAndShapeId(waypointId,shapeId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        ));
    }

    public BaseResponse postWaypoint(PostWaypointReq postWaypointReq, Integer shapeId) {
        ShapeEntity shapeEntity = shapeRepository.findById(shapeId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND));
        postWaypointReq.setGimbal_pitch_array_string(postWaypointReq.getGimbal_pitch_array().stream().map(String::valueOf).collect(Collectors.toList()).toString());
        WaypointEntity waypointEntity = WaypointEntity.from(postWaypointReq, shapeEntity);
        Integer id = waypointRepository.save(waypointEntity).getId();

        return new BaseResponse(ErrorCode.CREATED,waypointEntity.toDto());
    }

    public BaseResponse deleteWayPoint(Integer waypoint_id,Integer shapeId) {

        WaypointEntity waypointEntity = waypointRepository.findByIdAndShapeId(waypoint_id,shapeId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        waypointRepository.delete(waypointEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(waypoint_id)+"번 Waypoint가 삭제되었습니다.");
    }

    public BaseResponse getWaypointsByMissionId(Integer missionId) {
        List<WaypointEntity> results = waypointRepository.findByMissionId(missionId);
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(WaypointEntity::toDto).collect(Collectors.toList()));
    }
}
