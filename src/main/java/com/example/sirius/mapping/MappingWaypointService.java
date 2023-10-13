package com.example.sirius.mapping;


import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.mapping.domain.MappingEntity;
import com.example.sirius.mapping.domain.MappingWayPointEntity;
import com.example.sirius.mapping.domain.PostMappingWaypointReq;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MappingWaypointService {
    private MappingWaypointRepository mappingWaypointRepository;
    private MappingRepository mappingRepository;
    public BaseResponse getMappingWaypoints(Integer missionId) {
        return new BaseResponse(ErrorCode.SUCCESS, mappingWaypointRepository.findAllByMissionId(missionId));
    }

    public BaseResponse getMappingWaypointById(Integer waypointId, Integer missionId) {
        return new BaseResponse(ErrorCode.SUCCESS, mappingWaypointRepository.findByIdAndMissionId(waypointId,missionId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        ));
    }

    public BaseResponse postMappingWaypoint(PostMappingWaypointReq postMappingWaypointReq, Integer missionId) {
        MappingEntity mappingEntity = mappingRepository.findById(missionId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        );

        /* mission_id 와 seq 를 조회해서 seq가 중간에 들어올 시 update해야 함.*/
        mappingWaypointRepository.incrementSeqGreaterThan(missionId, postMappingWaypointReq.getSeq());

        MappingWayPointEntity mappingWayPointEntity = MappingWayPointEntity.from(postMappingWaypointReq, mappingEntity);
        Integer id = mappingWaypointRepository.save(mappingWayPointEntity).getId();

        return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(id)+"번 waypoint가 생성되었습니다.");
    }

    public BaseResponse deleteMappingWayPoint(Integer waypoint_id,Integer misison_id) {
        /* mission_id 와 seq 를 조회해서 seq가 중간에 들어올 시 update해야 함.*/
        MappingWayPointEntity mappingWayPointEntity = mappingWaypointRepository.findByIdAndMissionId(waypoint_id,misison_id).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        mappingWaypointRepository.decrementSeqGreaterThan(mappingWayPointEntity.getMappingEntity().getId(), mappingWayPointEntity.getSeq());

        mappingWaypointRepository.delete(mappingWayPointEntity);

        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(waypoint_id)+"번 Waypoint가 삭제되었습니다.");
    }
}
