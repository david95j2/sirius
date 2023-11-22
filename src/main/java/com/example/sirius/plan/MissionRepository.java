package com.example.sirius.plan;

import com.example.sirius.exception.BaseResponse;
import com.example.sirius.plan.domain.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<MissionEntity, Integer> {

    @Query("select m from MissionEntity m " +
            "join m.mapGroupEntity mg " +
            "where m.id=:missionId and mg.id=:mapGroupId")
    Optional<MissionEntity> findByIdAndMapGroupId(@Param("missionId") Integer missionId,@Param("mapGroupId") Integer mapGroupId);

    @Query("select m from MissionEntity m " +
            "join m.mapGroupEntity mg " +
            "where mg.id=:mapGroupId and m.groupNum=:groupNum")
    List<MissionEntity> findAllByMapGroupIdAndGroupNum(@Param("mapGroupId") Integer mapGroupId,@Param("groupNum") Integer groupNum);

    @Query("select m from MissionEntity m where m.mapGroupEntity.id=:mapGroupId")
    List<MissionEntity> findAllByMapGroupId(@Param("mapGroupId") Integer mapGroupId);
}
