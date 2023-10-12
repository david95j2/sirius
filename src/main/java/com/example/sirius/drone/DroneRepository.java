package com.example.sirius.drone;


import com.example.sirius.drone.domain.DroneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends JpaRepository<DroneEntity, Integer> {
    @Query("select d from DroneEntity d " +
            "join d.userEntity u where u.loginId=:loginId")
    List<DroneEntity> findAllByLoginId(@Param("loginId")String loginId);
    @Modifying
    @Query(value = "delete d from drones d " +
            "join users u on u.id=d.user_id " +
            "where d.id=:droneId and u.login_id=:loginId",
            nativeQuery = true)
    Integer deleteByIdAndLoginId(@Param("droneId") Integer droneId,@Param("loginId") String loginId);
    @Query("select d from DroneEntity d join d.userEntity u " +
            "where d.id=:droneId and u.loginId=:loginId")
    Optional<DroneEntity> findByIdAndLoginId(@Param("droneId") Integer droneId,@Param("loginId") String loginId);
}
