package com.example.sirius.map;

import com.example.sirius.facility.domain.ThumbnailEntity;
import com.example.sirius.map.domain.MapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MapRepository extends JpaRepository<MapEntity, Integer> {
    @Query("select t from FacilityEntity f join f.thumbnailEntities t " +
            "where f.id=:facilityId order by t.thumbnailRegdate desc limit 1")
    Optional<ThumbnailEntity> findByfacilityId(@Param("facilityId") Integer facilityId);
}
