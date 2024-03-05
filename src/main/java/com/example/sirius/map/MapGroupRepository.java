package com.example.sirius.map;

import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MapGroupRepository extends JpaRepository<MapGroupEntity, Integer> {
    @Query("select m.mapGroupEntity from MapEntity m join m.mapGroupEntity.facilityEntity f " +
            "where f.id=:facilityId and m.id=:mapId")
    Optional<MapGroupEntity> findByMapIdAndFacilityId(@Param("mapId") Integer mapId, @Param("facilityId") Integer facilityId);

    @Modifying
    @Query("delete from MapGroupEntity mg where mg.facilityEntity.id=:facilityId")
    Integer deleteAllByFacilityId(@Param("facilityId") Integer facilityId);

    @Query("select m from MapGroupEntity m join m.facilityEntity f " +
            "where f.id=:facilityId and m.id=:mapGroupId")
    Optional<MapGroupEntity> findByIdAndFacilityId(@Param("mapGroupId") Integer mapGroupId, @Param("facilityId") Integer facilityId);
}
