package com.example.sirius.plan;


import com.example.sirius.album.analysis.domain.AnalysisEntity;
import com.example.sirius.album.picture.domain.AlbumEntity;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.map.MapRepository;
import com.example.sirius.map.MapService;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import com.example.sirius.plan.domain.GetMissionRes;
import com.example.sirius.plan.domain.MissionEntity;
import com.example.sirius.plan.domain.PatchMissionReq;
import com.example.sirius.plan.domain.PostMissionReq;
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
    private MapService mapService;
    private MapRepository mapRepository;

    public BaseResponse getMissions(Integer mapId, Integer groupNum) {
        Integer mapGroupId = mapService.getMapGroupIdByMapId(mapId);

        JPAQuery<MissionEntity> query = queryFactory.selectFrom(missionEntity)
                .where(missionEntity.mapGroupEntity.id.eq(mapGroupId));

        if (groupNum != null) {
            query.where(missionEntity.groupNum.eq(groupNum));
        }

        List<MissionEntity> results = query.fetch();
        return new BaseResponse(ErrorCode.SUCCESS, results.stream().map(MissionEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getMissionById(Integer missionId, Integer mapId) {
        Integer mapGroupId = mapService.getMapGroupIdByMapId(mapId);
        return new BaseResponse(ErrorCode.SUCCESS,
                missionRepository.findByIdAndMapId(missionId, mapGroupId).orElseThrow(
                        () -> new AppException(ErrorCode.DATA_NOT_FOUND)
                ));
    }

    public BaseResponse postMission(PostMissionReq postMissionReq, Integer mapId) {
        MapGroupEntity mapGroupEntity = mapRepository.findMapGroupByMapId(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        MissionEntity mission = MissionEntity.from(postMissionReq, mapGroupEntity);
        Integer createdNum = missionRepository.save(mission).getId();
        return new BaseResponse(ErrorCode.CREATED, Integer.valueOf(createdNum) + "번 미션이 생성되었습니다.");
    }

    public BaseResponse patchMission(PatchMissionReq patchMissionReq, Integer missionId, Integer mapId) {
        MissionEntity missionEntity = missionRepository.findByIdAndMapId(missionId, mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        if (patchMissionReq.getName() != null) {
            missionEntity.setName(patchMissionReq.getName());
        }
        if (patchMissionReq.getGroup_num() != null) {
            missionEntity.setGroupNum(patchMissionReq.getGroup_num());
        }
        Integer modifiedNum = missionRepository.save(missionEntity).getId();
        return new BaseResponse(ErrorCode.ACCEPTED, Integer.valueOf(modifiedNum) + "번 미션의 값이 변경되었습니다.");
    }

    @Transactional
    public BaseResponse deleteMission(Integer missionId, Integer mapId) {
        missionRepository.findByIdAndMapId(missionId, mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        missionRepository.deleteById(missionId);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(missionId) + "번 미션이 삭제되었습니다.");
    }

    public BaseResponse startFittingProgram(Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        // 분석 시작
        // c++ 키기
        ExecutorService localExecutorService = Executors.newSingleThreadExecutor();
        localExecutorService.execute(() -> {
            // 비동기
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("/home/sb/workspace/pcd-manager3/build/fit_area", mapEntity.getMapPath());

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
}
