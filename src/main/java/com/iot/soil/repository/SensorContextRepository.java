package com.iot.soil.repository;

import com.iot.soil.entity.SensorContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorContextRepository extends JpaRepository<SensorContext, Long> {
    List<SensorContext> findBySensorId(String sensorId);
}

