package com.iot.soil.repository;

import com.iot.soil.entity.UnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMappingRepository extends JpaRepository<UnitMapping, String> {
}

