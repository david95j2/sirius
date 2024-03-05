package com.example.sirius.plan;


import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.picture.AlbumRepository;
import com.example.sirius.album.picture.AlbumService;
import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.map.MapGroupRepository;
import com.example.sirius.map.MapRepository;
import com.example.sirius.map.MapService;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import com.example.sirius.plan.domain.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.example.sirius.plan.domain.QMissionEntity.missionEntity;

@Service
@AllArgsConstructor
@Slf4j
public class MissionService {
    private JPAQueryFactory queryFactory;
    private MissionRepository missionRepository;
    private MapRepository mapRepository;
    private PropertyRepository propertyRepository;
    private ShapeRepository shapeRepository;
    private WaypointRepository waypointRepository;
    private AlbumRepository albumRepository;
    private AlbumService albumService;

    public BaseResponse getMissions(Integer mapId, Integer groupNum) {

        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        JPAQuery<MissionEntity> query = queryFactory.selectFrom(missionEntity)
                .where(missionEntity.mapGroupEntity.id.eq(mapEntity.getMapGroupEntity().getId()))
                .orderBy(missionEntity.groupNum.asc());

        if (groupNum != null) {
            query.where(missionEntity.groupNum.eq(groupNum));
        }

        List<MissionEntity> results = query.fetch();
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(MissionEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getMissionById(Integer missionId, Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        return new BaseResponse(ErrorCode.SUCCESS,
                missionRepository.findByIdAndMapGroupId(missionId, mapEntity.getMapGroupEntity().getId()).orElseThrow(
                        () -> new AppException(ErrorCode.DATA_NOT_FOUND)
                ));
    }

    public BaseResponse getMissionOnlyId(Integer missionId) {
         return new BaseResponse(ErrorCode.SUCCESS,missionRepository.findById(missionId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND)));
    }

    public BaseResponse postMission(PostMissionReq postMissionReq, Integer mapId) {
        MapGroupEntity mapGroupEntity = mapRepository.findMapGroupByMapId(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        MissionEntity mission = MissionEntity.from(postMissionReq, mapGroupEntity);
        Integer createdNum = missionRepository.save(mission).getId();
        return new BaseResponse(ErrorCode.CREATED, mission.toDto());
    }

    public BaseResponse patchMission(PatchMissionReq patchMissionReq, Integer missionId, Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        MissionEntity missionEntity = missionRepository.findByIdAndMapGroupId(missionId, mapEntity.getMapGroupEntity().getId()).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        if (patchMissionReq.getName() != null) {
            missionEntity.setName(patchMissionReq.getName());
        }
        if (patchMissionReq.getGroup_num() != null) {
            missionEntity.setGroupNum(patchMissionReq.getGroup_num());
        }
        Integer modifiedNum = missionRepository.save(missionEntity).getId();
        return new BaseResponse(ErrorCode.ACCEPTED, missionEntity.toDto());
    }

    @Transactional
    public BaseResponse deleteMission(Integer missionId, Integer mapId,Integer groupNum) {

        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        Integer mapGroupId = mapEntity.getMapGroupEntity().getId();
        if (groupNum != null) {
            List<MissionEntity> missionEntities = missionRepository.findAllByMapGroupIdAndGroupNum(mapGroupId,groupNum);
            missionEntities.stream().forEach(x -> deleteMissionLogic(x.getId()));
            return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(groupNum)+"번 그룹 미션이 삭제되었습니다.");
        } else {
            missionRepository.findByIdAndMapGroupId(missionId, mapGroupId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
            deleteMissionLogic(missionId);
            return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(missionId) + "번 미션이 삭제되었습니다.");
        }
    }

    @Transactional
    public void deleteMissionByMapGroupId(Integer mapGroupId) {
        List<MissionEntity> missionEntities = missionRepository.findAllByMapGroupId(mapGroupId);
        missionEntities.stream().forEach(x -> deleteMissionLogic(x.getId()));
    }

    @Transactional
    public void deleteMissionLogic(Integer missionId) {
        // waypoints 있으면 지우기
        waypointRepository.deleteByMissionId(missionId);
        // properties 있으면 지우기
        propertyRepository.deleteByMissionId(missionId);
        // shapes 있으면 지우기
        shapeRepository.deleteByMissionId(missionId);

        AlbumEntity albumEntity = albumRepository.findById(missionId).orElse(null);
        if (albumEntity != null) {
            albumService.deleteAlbumLogic(albumEntity.getId());
        }

        missionRepository.deleteById(missionId);
    }

    public BaseResponse startFittingProgram(PostFittingReq postFittingReq,Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        // 분석 시작
        // c++ 키기
        ExecutorService localExecutorService = Executors.newSingleThreadExecutor();
        localExecutorService.execute(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("/home/sb/workspace/pcd-manager3/build/fit_area", mapEntity.getMapPath(),postFittingReq.getPort());

            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                log.info("[Fit area Program] Exited with code: " + exitCode);

            } catch (IOException | InterruptedException e) {
                log.error("[Fit area Program] Error occurred while executing external process",e);
            }
        });
        localExecutorService.shutdown();


        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(mapId) + "번 점검 경로 생성을 시작합니다.");
    }

    public BaseResponse getWaypointsByMissionId(Integer missionId) {
        return new BaseResponse(ErrorCode.SUCCESS, waypointRepository.findByMissionId(missionId));
    }
}
