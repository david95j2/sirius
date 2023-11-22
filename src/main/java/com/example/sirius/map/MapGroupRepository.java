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
    Optional<MapGroupEntity> findByIdAndFacilityId(@Param("mapId") Integer mapId, @Param("facilityId") Integer facilityId);

    @Modifying
    @Query("delete from MapGroupEntity mg where mg.facilityEntity.id=:facilityId")
    Integer deleteAllByFacilityId(@Param("facilityId") Integer facilityId);


}
