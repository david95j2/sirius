package com.example.sirius.drone;


import com.example.sirius.drone.domain.DroneEntity;
import com.example.sirius.drone.domain.PatchDroneReq;
import com.example.sirius.drone.domain.PatchDroneRes;
import com.example.sirius.drone.domain.PostDroneReq;
import com.example.sirius.exception.AppException;
import com.example.sirius.exception.BaseResponse;
import com.example.sirius.exception.ErrorCode;
import com.example.sirius.user.UserService;
import com.example.sirius.user.domain.UserEntity;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DroneService {
    private DroneRepository droneRepository;
    private UserService userService;
    public BaseResponse getDrones(String login_id) {
        return new BaseResponse(ErrorCode.SUCCESS, droneRepository.findAllByLoginId(login_id));
    }

    public BaseResponse getDroneById(Integer drone_id,String login_id) {
        return new BaseResponse(ErrorCode.SUCCESS, droneRepository.findByIdAndLoginId(drone_id,login_id));
    }

    public BaseResponse postDrone(@Valid PostDroneReq postDroneReq, String login_id) {
        UserEntity userEntity = (UserEntity) userService.getUserByLoginId(login_id).getResult();
        DroneEntity droneEntity = DroneEntity.from(postDroneReq,userEntity);
        Integer drone_id = droneRepository.save(droneEntity).getId();
        return new BaseResponse(ErrorCode.CREATED,Integer.valueOf(drone_id)+"번 드론이 생성되었습니다.");
    }

    public BaseResponse patchDroneById(PatchDroneReq patchDroneReq, Integer drone_id, String login_id) {
        DroneEntity droneEntity = droneRepository.findByIdAndLoginId(drone_id,login_id).orElseThrow(()->new AppException(ErrorCode.DATA_NOT_FOUND));
        // 값이 있는 것만 수정
        if (patchDroneReq.getMin() != null) {
            droneEntity.setDroneVoltageMin(patchDroneReq.getMin());
        }

        if (patchDroneReq.getMax() != null) {
            droneEntity.setDroneVoltageMax(patchDroneReq.getMax());
        }

        if (patchDroneReq.getName() != null) {
            droneEntity.setDroneType(patchDroneReq.getName());
        }

        if (patchDroneReq.getX_dimension() != null) {
            droneEntity.setXDimension(patchDroneReq.getX_dimension());
        }

        if (patchDroneReq.getY_dimension() != null) {
            droneEntity.setYDimension(patchDroneReq.getY_dimension());
        }

        if (patchDroneReq.getZ_dimension() != null) {
            droneEntity.setZDimension(patchDroneReq.getZ_dimension());
        }

        // Save the updated entity
        DroneEntity updated = droneRepository.save(droneEntity);
        PatchDroneRes patchDroneRes = updated.toDto();

        return new BaseResponse(ErrorCode.SUCCESS, patchDroneRes);
    }

    @Transactional
    public BaseResponse deleteDroneById(Integer drone_id, String login_id) {
        Integer deletedCount = droneRepository.deleteByIdAndLoginId(drone_id,login_id);
        if (deletedCount != 0) {
            return new BaseResponse(ErrorCode.SUCCESS,Integer.valueOf(drone_id)+"번 드론이 삭제되었습니다.");
        } else {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
    }
}
