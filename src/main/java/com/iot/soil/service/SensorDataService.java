package com.iot.soil.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.soil.dto.request.SensorObservationRequest;
import com.iot.soil.entity.*;
import com.iot.soil.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataService {

    private final SensorRepository sensorRepository;
    private final MeasurementTypeRepository measurementTypeRepository;
    private final SensorDataRepository sensorDataRepository;
    private final SensorMeasurementTypeRepository sensorMeasurementTypeRepository;
    private final SensorContextService sensorContextService;
    private final UnitMappingRepository unitMappingRepository;
    private final ObjectMapper objectMapper;

    private Map<String, String> unitMap;

    @PostConstruct
    private void loadUnitMappings() {
        unitMap = new HashMap<>();
        unitMappingRepository.findAll().forEach(mapping ->
            unitMap.put(mapping.getUnitCode(), mapping.getUnitLabel())
        );
        log.info("Loaded {} unit mappings from database", unitMap.size());
    }

    public void refreshUnitMappings() {
        loadUnitMappings();
        log.info("Unit mappings refreshed successfully");
    }


    @Transactional
    public String processSensorObservation(SensorObservationRequest request) {
        log.info("Processing observation for sensor: {}", request.getMadeBySensor().getId());

        try {
            String sensorId = extractSensorId(request.getMadeBySensor().getId());
            String sensorName = request.getMadeBySensor().getLabel();
            String location = request.getHasFeatureOfInterest().getLocation();

            // 1. Sačuvaj ili ažuriraj senzor
            Sensor sensor = saveOrUpdateSensor(sensorId, sensorName, location);

            // 2. Sačuvaj context podatke
            sensorContextService.saveContextData(request);

            // 3. Procesiraj svako merenje
            for (SensorObservationRequest.Observation observation : request.getHasMember()) {
                processObservation(sensor, observation, location);
            }

            log.info("Successfully saved observation for sensor: {}", sensorId);
            return "Observation saved successfully";

        } catch (Exception e) {
            log.error("Error processing observation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process observation: " + e.getMessage());
        }
    }

    private String extractSensorId(String fullId) {
        if (fullId.contains(":")) {
            return fullId.substring(fullId.indexOf(":") + 1);
        }
        return fullId;
    }

    private Sensor saveOrUpdateSensor(String sensorId, String sensorName, String location) {
        Optional<Sensor> existingSensor = sensorRepository.findById(sensorId);

        if (existingSensor.isPresent()) {
            Sensor sensor = existingSensor.get();
            sensor.setSensorName(sensorName);
            sensor.setLocation(location);
            sensor.setIsActive(true);
            return sensorRepository.save(sensor);
        } else {
            Sensor sensor = Sensor.builder()
                    .sensorId(sensorId)
                    .sensorName(sensorName)
                    .location(location)
                    .isActive(true)
                    .build();
            return sensorRepository.save(sensor);
        }
    }

    private void processObservation(Sensor sensor,
                                    SensorObservationRequest.Observation observation,
                                    String location) throws JsonProcessingException {

        String measurementTypeId = observation.getObservedProperty().getId();
        Double value = observation.getHasResult().getNumericValue();
        String unit = observation.getHasResult().getUnit();

        // Parse timestamp directly as Instant (UTC)
        Instant observationTime = Instant.parse(observation.getPhenomenonTime());

        // 1. Sačuvaj ili ažuriraj tip merenja
        MeasurementType measurementType = saveOrUpdateMeasurementType(measurementTypeId, unit);

        // 2. Poveži senzor i tip merenja (koristi Instant UTC)
        linkSensorToMeasurementType(sensor.getSensorId(), measurementType.getTypeId(), observationTime);

        // 3. Sačuvaj podatke
        saveSensorData(sensor.getSensorId(), measurementType.getTypeId(),
                value, unit, observationTime, location, observation);
    }

    private MeasurementType saveOrUpdateMeasurementType(String typeId, String unit) {
        String displayName = convertTypeIdToDisplayName(typeId);
        String unitLabel = unitMap.getOrDefault(unit, "");

        Optional<MeasurementType> existingType = measurementTypeRepository.findById(typeId);

        if (existingType.isPresent()) {
            MeasurementType type = existingType.get();
            if (unit != null) {
                type.setUnit(unit);
                type.setUnitLabel(unitLabel);
            }
            return measurementTypeRepository.save(type);
        } else {
            MeasurementType type = MeasurementType.builder()
                    .typeId(typeId)
                    .displayName(displayName)
                    .unit(unit)
                    .unitLabel(unitLabel)
                    .dataType("NUMERIC")
                    .build();
            return measurementTypeRepository.save(type);
        }
    }

    private String convertTypeIdToDisplayName(String typeId) {
        String[] parts = typeId.split(":");
        if (parts.length > 1) {
            String name = parts[1];
            return name.replaceAll("([a-z])([A-Z])", "$1 $2");
        }
        return typeId;
    }

    private void linkSensorToMeasurementType(String sensorId, String measurementTypeId, Instant observationTime) {
        SensorMeasurementTypeId id = new SensorMeasurementTypeId(sensorId, measurementTypeId);

        Optional<SensorMeasurementType> existingLink = sensorMeasurementTypeRepository.findById(id);

        if (existingLink.isPresent()) {
            SensorMeasurementType link = existingLink.get();
            link.setLastObserved(observationTime);
            sensorMeasurementTypeRepository.save(link);
        } else {
            SensorMeasurementType link = SensorMeasurementType.builder()
                    .id(id)
                    .isActive(true)
                    .lastObserved(observationTime)
                    .build();
            sensorMeasurementTypeRepository.save(link);
        }
    }

    private void saveSensorData(String sensorId, String measurementTypeId,
                                Double value, String unit, Instant timestamp,
                                String location, SensorObservationRequest.Observation observation)
            throws JsonProcessingException {

        String rawData = objectMapper.writeValueAsString(observation);

        SensorData sensorData = SensorData.builder()
                .sensorId(sensorId)
                .measurementType(measurementTypeId)
                .value(value)
                .unit(unit)
                .timestamp(timestamp) // Use Instant directly
                .location(location)
                .rawData(rawData)
                .build();

        sensorDataRepository.save(sensorData);
    }
}