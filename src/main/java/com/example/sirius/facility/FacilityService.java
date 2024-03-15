package com.example.sirius.facility;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.facility.domain.*;
import com.example.sirius.map.MapGroupRepository;
import com.example.sirius.map.MapRepository;
import com.example.sirius.mapping.MappingService;
import com.example.sirius.plan.MissionRepository;
import com.example.sirius.plan.MissionService;
import com.example.sirius.plan.domain.MissionEntity;
import com.example.sirius.user.UserRepository;
import com.example.sirius.user.UserService;
import com.example.sirius.user.domain.GetUsersRes;
import com.example.sirius.user.domain.UserEntity;
import com.example.sirius.utils.SiriusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FacilityService {
    private FacilityRepository facilityRepository;
    private UserService userService;
    private MissionService missionService;
    private MappingService mappingService;
    private UserRepository userRepository;
    private MapGroupRepository mapGroupRepository;
    private MissionRepository missionRepository;
    private MapRepository mapRepository;
    private ThumbnailRepository thumbnailRepository;
    public BaseResponse getFacilities(String loginId) {
        userService.getUserByLoginId(loginId);
        List<FacilityEntity> result = facilityRepository.findAll();
        return new BaseResponse(ErrorCode.SUCCESS,result.stream().map(FacilityEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getFacilityByIdAndLoginId(Integer facilityId, String loginId) {
        FacilityEntity facilityEntity = facilityRepository.findByIdAndLoginId(facilityId,loginId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        return new BaseResponse(ErrorCode.SUCCESS,facilityEntity.toDto());
    }

    public BaseResponse postFacility(PostFacilityReq postFacilityReq, String loginId) {
        UserEntity userEntity = userRepository.findByLoginId(loginId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        FacilityEntity comparedName = facilityRepository.findByNameAndLoginId(postFacilityReq.getName(), postFacilityReq.getLocation()).orElse(null);

        if (comparedName != null) {
            PatchFacilityReq patchFacilityReq = new PatchFacilityReq();
            patchFacilityReq.setLatitude(postFacilityReq.getLatitude());
            patchFacilityReq.setLongitude(postFacilityReq.getLongitude());
            return patchFacility(patchFacilityReq, comparedName.getId(),loginId,true);
        } else { // 장소가 없으면 post
            if (postFacilityReq.getLocation() == null && postFacilityReq.getName() == null) {
                FacilityEntity facilityEntity = FacilityEntity.from_null(userEntity);
                facilityRepository.save(facilityEntity).getId();
                PatchFacilityRes patchFacilityRes = facilityEntity.toDto();
                return new BaseResponse(ErrorCode.CREATED, patchFacilityRes);
            } else {
                String unicode = SiriusUtils.stringToUnicode(postFacilityReq.getLocation()).replace("\\","");
                postFacilityReq.setLocationAscii(unicode);
                FacilityEntity facilityEntity = FacilityEntity.from(postFacilityReq,userEntity);
                facilityRepository.save(facilityEntity).getId();
                PatchFacilityRes patchFacilityRes = facilityEntity.toDto();
                return new BaseResponse(ErrorCode.CREATED, patchFacilityRes);
            }


        }
    }

    public BaseResponse patchFacility(PatchFacilityReq patchFacilityReq, Integer locationId, String loginId, Boolean exist) {
        FacilityEntity facilityEntity = facilityRepository.findByIdAndLoginId(locationId, loginId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOT_FOUND)
        );
        if (patchFacilityReq.getName() != null) {
            facilityEntity.setName(patchFacilityReq.getName());
        }
        if (patchFacilityReq.getLatitude() != null) {
            facilityEntity.setLatitude(patchFacilityReq.getLatitude());
        }
        if (patchFacilityReq.getLongitude() != null) {
            facilityEntity.setLongitude(patchFacilityReq.getLongitude());
        }
        if (patchFacilityReq.getDescription() != null) {
            facilityEntity.setDescription(patchFacilityReq.getDescription());
        }

        FacilityEntity updated = facilityRepository.save(facilityEntity);
        PatchFacilityRes patchFacilityRes = updated.toDto();
        if (exist) {
            return new BaseResponse(ErrorCode.EXIST_ACCEPTED, patchFacilityRes);
        } else {
            return new BaseResponse(ErrorCode.ACCEPTED, patchFacilityRes);
        }
    }

    @Transactional
    public BaseResponse deleteFacility(Integer facilityId, String loginId) {
        // 시설물있는지 확인
        FacilityEntity facilityEntity = facilityRepository.findByIdAndLoginId(facilityId,loginId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));

        // 매핑 미션있으면 지우기
        facilityEntity.getMappingEntities().stream().forEach(x -> mappingService.deleteMappingLogic(x.getId()));
        // 썸네일있으면 지우기
        thumbnailRepository.deleteAllByFacilityId(facilityId);
        // 맵있으면 지우기
        mapRepository.deleteAllByFacilityId(facilityId);
        // 미션있으면 지우기
        facilityEntity.getMapGroupEntities().stream().forEach(x -> missionService.deleteMissionByMapGroupId(x.getId()));
        List<MissionEntity> result = missionRepository.findAll();

        // 맵 그룹있으면 지우기
        mapGroupRepository.deleteAllByFacilityId(facilityId);

        facilityRepository.delete(facilityEntity);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(facilityId)+"번 시설물이 삭제되었습니다.");
    }

    public Integer postFacilityThumbnails(PostThumbnails postThumbnails, Integer locationId) {
        FacilityEntity facilityEntity = facilityRepository.findById(locationId).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        // 같은 경로 및 파일이 존재하면 에러처리
        ThumbnailEntity exists = thumbnailRepository.findByPath(postThumbnails.getFile_path()).orElse(null);

        if (exists == null) {
            ThumbnailEntity thumbnailEntity = ThumbnailEntity.from(postThumbnails, facilityEntity);
            return thumbnailRepository.save(thumbnailEntity).getId();
        } else {
            return -1;
        }
    }

    public BaseResponse postLocationThumbnail(MultipartFile file, Integer facilityId) {
        FacilityEntity facilityEntity = facilityRepository.findById(facilityId).orElseThrow(()-> new AppException(ErrorCode.DATA_NOT_FOUND));
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String formattedDate = sdf.format(now);
        String url = facilityEntity.getUserEntity().getLoginId() + "/" + SiriusUtils.stringToUnicode(facilityEntity.getName()).replace("\\","") + "/" + formattedDate.split("_")[0] + "/" + formattedDate.split("_")[1]+"/pcd/samples";
        String os_path = Paths.get("/hdd_ext/part6", "sirius",url).toString();

        SiriusUtils.saveResizedImage(file, os_path, 320, 200);
        PostThumbnails postThumbnails = new PostThumbnails();
        postThumbnails.setFile_path(String.valueOf(Paths.get(os_path,file.getOriginalFilename())));
        String thumb_datetime = formattedDate.split("_")[0] + "_" + formattedDate.split("_")[1];
        postThumbnails.setRegdate(thumb_datetime);

        ThumbnailEntity thumbnailEntity = ThumbnailEntity.from(postThumbnails, facilityEntity);
        ThumbnailEntity createdThumbnailEntity = thumbnailRepository.save(thumbnailEntity);
        return new BaseResponse(ErrorCode.CREATED, createdThumbnailEntity);
    }
}
