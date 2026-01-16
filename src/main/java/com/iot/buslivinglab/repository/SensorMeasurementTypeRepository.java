package com.iot.soil.repository;

import com.iot.buslivinglab.entity.SensorMeasurementType;
import com.iot.buslivinglab.entity.SensorMeasurementTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorMeasurementTypeRepository extends JpaRepository<SensorMeasurementType, SensorMeasurementTypeId> {

    Optional<SensorMeasurementType> findById(SensorMeasurementTypeId id);

    default Optional<SensorMeasurementType> findBySensorIdAndMeasurementType(String sensorId, String measurementType) {
        SensorMeasurementTypeId id = new SensorMeasurementTypeId(sensorId, measurementType);
        return findById(id);
    }

    default boolean existsBySensorIdAndMeasurementType(String sensorId, String measurementType) {
        SensorMeasurementTypeId id = new SensorMeasurementTypeId(sensorId, measurementType);
        return existsById(id);
    }
}
