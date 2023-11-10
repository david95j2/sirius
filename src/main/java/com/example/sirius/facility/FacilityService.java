package com.example.sirius.facility;

import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.facility.domain.*;
import com.example.sirius.user.UserRepository;
import com.example.sirius.user.UserService;
import com.example.sirius.user.domain.GetUsersRes;
import com.example.sirius.user.domain.UserEntity;
import com.example.sirius.utils.SiriusUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FacilityService {
    private FacilityRepository facilityRepository;
    private UserService userService;
    private UserRepository userRepository;
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
            String unicode = SiriusUtils.stringToUnicode(postFacilityReq.getLocation()).replace("\\","");
            postFacilityReq.setLocationAscii(unicode);
            FacilityEntity facilityEntity = FacilityEntity.from(postFacilityReq,userEntity);
            facilityRepository.save(facilityEntity).getId();
            PatchFacilityRes patchFacilityRes = facilityEntity.toDto();
            return new BaseResponse(ErrorCode.CREATED, patchFacilityRes);
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
}
