package com.example.sirius.map;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.facility.FacilityRepository;
import com.example.sirius.facility.ThumbnailRepository;
import com.example.sirius.facility.domain.FacilityEntity;
import com.example.sirius.facility.domain.PostThumbnails;
import com.example.sirius.facility.domain.ThumbnailEntity;
import com.example.sirius.map.domain.GetMapsRes;
import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import com.example.sirius.map.domain.PostMapReq;
import com.example.sirius.plan.MissionService;
import com.example.sirius.user.UserService;
import com.example.sirius.utils.SiriusUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sirius.map.domain.QMapEntity.mapEntity;

@Service
@AllArgsConstructor
public class MapService {
    private JPAQueryFactory queryFactory;
    private UserService userService;
    private MapRepository mapRepository;
    private MissionService missionService;
    private FacilityRepository facilityRepository;
    private MapGroupRepository mapGroupRepository;

    public BaseResponse getMaps(Integer facilityId, String loginId, String date, Integer time) {
        userService.getUserByLoginId(loginId);
        JPAQuery<MapEntity> query = queryFactory.selectFrom(mapEntity)
                .where(mapEntity.mapGroupEntity.facilityEntity.id.eq(facilityId));


        if (date != null) {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
            query.where(mapEntity.date.eq(parsedDate));
        }

        if (time != null) {
            if (time >= 24) {
                throw new AppException(ErrorCode.TIME_METHOD_NOT_ALLOWED);
            }
            LocalTime startTime = LocalTime.of(time, 0);
            LocalTime endTime = LocalTime.of(23, 59, 59);
            query.where(mapEntity.time.between(startTime, endTime));
        }

        List<MapEntity> results = query.fetch();

        List<GetMapsRes> new_results = results.stream().map(x -> {
            String fileName = Paths.get(x.getMapPath()).getFileName().toString();
            GetMapsRes getMapsRes = new GetMapsRes();
            getMapsRes.setId(x.getId());
            getMapsRes.setFile_path(fileName);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime combinedDateTime = LocalDateTime.of(x.getDate(), x.getTime());
            String formattedDate = combinedDateTime.format(formatter);
            getMapsRes.setRegdate(formattedDate);
            return getMapsRes;
        }).collect(Collectors.toList());

        return new BaseResponse(ErrorCode.SUCCESS, new_results);
    }

    public Resource getMapFileById(Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return SiriusUtils.loadFileAsResource(Paths.get(mapEntity.getMapPath()).getParent().toString(),
                Paths.get(mapEntity.getMapPath()).getFileName().toString());
    }

    public Resource getLocationThumbnail(Integer facility_id) {
        ThumbnailEntity thumbnailEntity = mapRepository.findByfacilityId(facility_id).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        return SiriusUtils.loadFileAsResource(Path.of(thumbnailEntity.getThumbnailPath()).getParent().toString(),
                Path.of(thumbnailEntity.getThumbnailPath()).getFileName().toString());
    }


    public Integer getMapGroupIdByMapId(Integer mapId) {
        return mapRepository.findMapGroupIdByMapId(mapId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
    }

    public Boolean getMapsByLocationIdAndDate(Integer facilityId, String regdate) {
        LocalDate date = LocalDate.parse(regdate.split("_")[0],DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalTime time = LocalTime.parse(regdate.split("_")[1],DateTimeFormatter.ofPattern("HHmmss"));
        List<MapEntity> results = mapRepository.findByLocationIdAndDatetime(facilityId,date,time);
        if (results.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Integer postMapGroup(Integer facilityId) {
        FacilityEntity facilityEntity = facilityRepository.findById(facilityId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));

        MapGroupEntity mapGroupEntity = MapGroupEntity.builder().facilityEntity(facilityEntity).build();
        return mapGroupRepository.save(mapGroupEntity).getId();
    }

    public Integer postMaps(PostMapReq postMapReq, Integer mapGroupId, Integer facilityId) {
        // location
        MapGroupEntity mapGroupEntity = mapGroupRepository.findByIdAndFacilityId(mapGroupId,facilityId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        );
        MapEntity mapEntity = MapEntity.from(postMapReq,mapGroupEntity);
        return mapRepository.save(mapEntity).getId();
    }

    @Transactional
    public BaseResponse deleteMap(Integer facilityId, Integer mapId) {

        MapGroupEntity mapGroupEntity = mapGroupRepository.findByMapIdAndFacilityId(mapId,facilityId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        // 맵 있으면 지우기
        mapRepository.deleteAllByMapGroupId(mapGroupEntity.getId());

        // 미션있으면 지우기
        missionService.deleteMissionByMapGroupId(mapGroupEntity.getId());

        // 맵 그룹있으면 지우기
        mapGroupRepository.delete(mapGroupEntity);

        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(mapId)+"번 맵이 삭제되었습니다.");
    }

    public BaseResponse getMapById(Integer mapId) {
        MapEntity mapEntity = mapRepository.findById(mapId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        String fileName = Paths.get(mapEntity.getMapPath()).getFileName().toString();
        GetMapsRes getMapsRes = new GetMapsRes();
        getMapsRes.setId(mapEntity.getId());
        getMapsRes.setFile_path(fileName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime combinedDateTime = LocalDateTime.of(mapEntity.getDate(), mapEntity.getTime());
        String formattedDate = combinedDateTime.format(formatter);
        getMapsRes.setRegdate(formattedDate);

        return new BaseResponse(ErrorCode.SUCCESS, getMapsRes);
    }
}
