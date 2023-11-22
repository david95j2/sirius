package com.example.sirius.facility;

import com.example.sirius.facility.domain.ThumbnailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ThumbnailRepository extends JpaRepository<ThumbnailEntity,Integer> {
    @Query("select t from ThumbnailEntity t where t.thumbnailPath=:filePath")
    Optional<ThumbnailEntity> findByPath(@Param("filePath") String filePath);

    @Modifying
    @Query("delete from ThumbnailEntity t where t.facilityEntity.id=:facilityId")
    Integer deleteAllByFacilityId(@Param("facilityId") Integer facilityId);
}

