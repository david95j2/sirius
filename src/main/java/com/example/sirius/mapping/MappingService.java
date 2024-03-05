package com.example.sirius.mapping;


import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.facility.FacilityRepository;
import com.example.sirius.facility.domain.FacilityEntity;
import com.example.sirius.mapping.domain.MappingEntity;
import com.example.sirius.mapping.domain.PatchMappingReq;
import com.example.sirius.mapping.domain.PostMappingReq;
import com.example.sirius.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MappingService {
    private MappingRepository mappingRepository;
    private UserService userService;
    private FacilityRepository facilityRepository;

    public BaseResponse getMappings(Integer facilityId) {
        List<MappingEntity> result = mappingRepository.findAllByFacilityId(facilityId);
        return new BaseResponse(ErrorCode.SUCCESS, result.stream().map(MappingEntity::toDto).collect(Collectors.toList()));
    }

    public BaseResponse getMappingByIdAndFacilityId(Integer missionId, Integer facilityId) {
        MappingEntity mappingEntity = mappingRepository.findByIdAndFacilityId(missionId, facilityId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return new BaseResponse(ErrorCode.SUCCESS, mappingEntity.toDto());
    }

    public BaseResponse postMapping(PostMappingReq postMappingReq, Integer facilityId) {
        FacilityEntity facilityEntity = facilityRepository.findById(facilityId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        MappingEntity mappingEntity = MappingEntity.from(postMappingReq, facilityEntity);
        Integer mission_id = mappingRepository.save(mappingEntity).getId();
        return new BaseResponse(ErrorCode.CREATED, mappingEntity.toDto());
    }

    public BaseResponse patchMapping(PatchMappingReq patchMappingReq, Integer missionId, Integer facilityId) {
        MappingEntity mappingEntity = mappingRepository.findByIdAndFacilityId(missionId, facilityId).orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        if (patchMappingReq.getName() != null) {
            mappingEntity.setName(patchMappingReq.getName());
        }

        MappingEntity updated = mappingRepository.save(mappingEntity);
        return new BaseResponse(ErrorCode.ACCEPTED, updated.toDto());
    }

    @Transactional
    public BaseResponse deleteMapping(Integer missionId, Integer facilityId) {
        getMappingByIdAndFacilityId(missionId, facilityId);
        deleteMappingLogic(missionId);
        return new BaseResponse(ErrorCode.SUCCESS, Integer.valueOf(missionId) + "번 미션이 삭제되었습니다.");
    }
    
    @Transactional
    public void deleteMappingLogic(Integer missionId) {
        mappingRepository.deleteById(missionId);
    }
}
