package com.example.sirius.map;

import com.example.sirius.map.domain.MapEntity;
import com.example.sirius.map.domain.MapGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MapGroupRepository extends JpaRepository<MapGroupEntity, Integer> {
    @Query("select mg from MapGroupEntity mg join mg.facilityEntity f " +
            "where f.id=:facilityId and mg.id=:mapGroupId")
    Optional<MapGroupEntity> findByIdAndFacilityId(@Param("mapGroupId") Integer mapGroupId, @Param("facilityId") Integer facilityId);
}
