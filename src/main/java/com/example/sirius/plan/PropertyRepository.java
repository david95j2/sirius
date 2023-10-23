package com.example.sirius.plan;

import com.example.sirius.plan.domain.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Integer> {
}
