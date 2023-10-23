package com.example.sirius.mapping;


import com.example.sirius.mapping.domain.MappingWayPointEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MappingWaypointRepository extends JpaRepository<MappingWayPointEntity,Integer> {

    @Query("select w from MappingWayPointEntity w join w.mappingEntity m where m.id=:mappingId order by w.seq")
    List<MappingWayPointEntity> findAllByMissionId(@Param("mappingId") Integer mappingId);

    @Query("select w from MappingWayPointEntity w join w.mappingEntity m where m.id=:mappingId and w.id=:waypointId")
    Optional<MappingWayPointEntity> findByIdAndMissionId(@Param("waypointId") Integer waypointId, @Param("mappingId") Integer mappingId);

    @Transactional
    @Modifying
    @Query("update MappingWayPointEntity w set w.seq=w.seq+1 " +
            "where w.mappingEntity.id=:mappingId and w.seq>=:seq")
    void incrementSeqGreaterThan(@Param("mappingId") Integer mappingId, @Param("seq") Integer seq);

    @Transactional
    @Modifying
    @Query("update MappingWayPointEntity w set w.seq=w.seq-1 " +
            "where w.mappingEntity.id=:mappingId and w.seq>=:seq")
    void decrementSeqGreaterThan(@Param("mappingId") Integer mappingId, @Param("seq") Integer seq);
}
