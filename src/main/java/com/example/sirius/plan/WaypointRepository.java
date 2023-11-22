package com.example.sirius.plan;

import com.example.sirius.mapping.domain.MappingWayPointEntity;
import com.example.sirius.plan.domain.WaypointEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WaypointRepository extends JpaRepository<WaypointEntity, Integer> {
    @Query("select w from WaypointEntity w join w.shapeEntity s where s.id=:shapeId order by w.seq asc ")
    List<WaypointEntity> findAllByShapeId(@Param("shapeId") Integer shapeId);

    @Query("select w from WaypointEntity w join w.shapeEntity s where s.id=:shapeId and w.id=:waypointId")
    Optional<WaypointEntity> findByIdAndShapeId(@Param("waypointId") Integer waypointId, @Param("shapeId") Integer shapeId);

    @Transactional
    @Modifying
    @Query("update WaypointEntity w set w.seq=w.seq+1 " +
            "where w.shapeEntity.id=:shapeId and w.seq>=:seq")
    void incrementSeqGreaterThan(@Param("shapeId") Integer shapeId, @Param("seq") Integer seq);

    @Transactional
    @Modifying
    @Query("update WaypointEntity w set w.seq=w.seq-1 " +
            "where w.shapeEntity.id=:shapeId and w.seq>=:seq")
    void decrementSeqGreaterThan(@Param("shapeId") Integer shapeId, @Param("seq") Integer seq);

    @Query("select w from WaypointEntity w join w.shapeEntity.missionEntity m where m.id=:missionId order by w.seq asc")
    List<WaypointEntity> findByMissionId(@Param("missionId") Integer missionId);

    @Modifying
    @Query("delete from WaypointEntity w where w.shapeEntity.missionEntity.id=:missionId")
    Integer deleteByMissionId(@Param("missionId") Integer missionId);
}
