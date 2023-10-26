package com.example.sirius.plan;

import com.example.sirius.plan.domain.ShapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface ShapeRepository extends JpaRepository<ShapeEntity, Integer> {
    @Query("select s from ShapeEntity s " +
            "join s.missionEntity m where m.id=:missionId order by s.seq asc ")
    List<ShapeEntity> findByMissionId(@Param("missionId") Integer missionId);

    @Query("select s from ShapeEntity s " +
            "join s.missionEntity m " +
            "where s.id=:shapeId and m.id=:missionId")
    Optional<ShapeEntity> findByIdAndMissionId(@Param("shapeId") Integer shapeId,@Param("missionId") Integer missionId);
}
