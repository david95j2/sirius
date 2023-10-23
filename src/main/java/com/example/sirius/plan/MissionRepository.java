package com.example.sirius.plan;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.plan.domain.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<MissionEntity, Integer> {

    @Query("select m from MissionEntity m " +
            "join m.mapGroupEntity mg " +
            "where m.id=:missionId and mg.id=:mapGroupId")
    Optional<MissionEntity> findByIdAndMapId(@Param("missionId") Integer missionId,@Param("mapGroupId") Integer mapGroupId);
}
