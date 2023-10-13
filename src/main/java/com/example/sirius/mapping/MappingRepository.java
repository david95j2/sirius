package com.example.sirius.mapping;


import com.example.sirius.mapping.domain.MappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MappingRepository extends JpaRepository<MappingEntity,Integer> {

    @Query("select m from MappingEntity m " +
            "join m.facilityEntity f where f.id=:facilityId")
    List<MappingEntity> findAllByFacilityId(@Param("facilityId") Integer facilityId);

    @Query("select m from MappingEntity m " +
            "join m.facilityEntity f where m.id=:missionId and f.id=:facilityId")
    Optional<MappingEntity> findByIdAndFacilityId(@Param("missionId") Integer missionId, @Param("facilityId") Integer facilityId);

}
