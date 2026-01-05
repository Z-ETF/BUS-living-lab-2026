package com.iot.soil.service;

import com.iot.soil.dto.request.SensorObservationRequest;
import com.iot.soil.entity.SensorContext;
import com.iot.soil.repository.SensorContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorContextService {

    private final SensorContextRepository sensorContextRepository;

    /**
     * Saves context data from the observation request to the database.
     * Extracts all namespaces from @context and saves them per sensor.
     */
    public void saveContextData(SensorObservationRequest request) {
        String sensorId = extractSensorId(request.getMadeBySensor().getId());
        SensorObservationRequest.Context context = request.getContext();

        if (context != null) {
            // Check if context already exists for this sensor
            List<SensorContext> existingContext = sensorContextRepository.findBySensorId(sensorId);

            if (existingContext.isEmpty()) {
                // Save SOSA namespace
                if (context.getSosa() != null) {
                    saveSingleContext(sensorId, "sosa", context.getSosa());
                }
                // Save SSN namespace
                if (context.getSsn() != null) {
                    saveSingleContext(sensorId, "ssn", context.getSsn());
                }
                // Save QUDT namespace
                if (context.getQudt() != null) {
                    saveSingleContext(sensorId, "qudt", context.getQudt());
                }
                // Save Unit namespace
                if (context.getUnit() != null) {
                    saveSingleContext(sensorId, "unit", context.getUnit());
                }
                // Save XSD namespace
                if (context.getXsd() != null) {
                    saveSingleContext(sensorId, "xsd", context.getXsd());
                }
                // Save RDFS namespace
                if (context.getRdfs() != null) {
                    saveSingleContext(sensorId, "rdfs", context.getRdfs());
                }
                // Save LL (Living Lab) namespace
                if (context.getLl() != null) {
                    saveSingleContext(sensorId, "ll", context.getLl());
                }

                log.info("Saved context data for sensor: {}", sensorId);
            } else {
                log.info("Context already exists for sensor: {}, skipping save", sensorId);
            }
        }
    }

    /**
     * Saves a single context entry
     */
    private void saveSingleContext(String sensorId, String contextName, String contextUri) {
        SensorContext sensorContext = SensorContext.builder()
                .sensorId(sensorId)
                .contextName(contextName)
                .contextUri(contextUri)
                .build();

        sensorContextRepository.save(sensorContext);
        log.debug("Saved context: {} -> {} for sensor: {}", contextName, contextUri, sensorId);
    }

    /**
     * Extracts sensor ID from the full ID (removes namespace prefix like "ll:")
     */
    private String extractSensorId(String fullSensorId) {
        if (fullSensorId.contains(":")) {
            return fullSensorId.substring(fullSensorId.indexOf(":") + 1);
        }
        return fullSensorId;
    }
}

