package com.example.sirius.facility;

import com.example.sirius.facility.domain.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacilityRepository extends JpaRepository<FacilityEntity,Integer> {
    @Query("select f from FacilityEntity f join f.userEntity u where u.loginId=:loginId")
    List<FacilityEntity> findAllByLoginId(@Param("loginId") String loginId);

    @Query("select f from FacilityEntity f join f.userEntity u where u.loginId=:loginId and f.id=:facilityId")
    Optional<FacilityEntity> findByIdAndLoginId(@Param("facilityId") Integer facilityId,@Param("loginId") String loginId);

    @Query("select f from FacilityEntity f join f.userEntity u where f.name=:name and f.location=:location")
    Optional<FacilityEntity> findByNameAndLoginId(@Param("name") String name, @Param("location") String location);

    @Query("select f from FacilityEntity f where f.location=:location")
    Optional<FacilityEntity> findByLocation(@Param("location") String location);
}
