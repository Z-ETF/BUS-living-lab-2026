package com.iot.buslivinglab.repository;

import com.iot.buslivinglab.entity.UnitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMappingRepository extends JpaRepository<UnitMapping, String> {
}


