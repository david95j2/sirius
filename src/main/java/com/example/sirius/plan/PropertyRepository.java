package com.example.sirius.plan;

import com.example.sirius.plan.domain.PropertyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Integer> {
    @Modifying
    @Query("delete from PropertyEntity p where p.shapeEntity.missionEntity.id=:missionId")
    Integer deleteByMissionId(@Param("missionId") Integer missionId);
}
