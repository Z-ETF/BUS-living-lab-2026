package com.iot.buslivinglab.service;

import com.iot.buslivinglab.entity.MeasurementType;
import com.iot.buslivinglab.entity.UnitMapping;
import com.iot.buslivinglab.repository.MeasurementTypeRepository;
import com.iot.buslivinglab.repository.UnitMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitSyncService {

    private final UnitMappingRepository unitMappingRepository;
    private final MeasurementTypeRepository measurementTypeRepository;

    /**
     * Synchronizes unit_label values in measurement_types table with unit_mappings table.
     * This ensures that when unit_mappings values are updated, all measurement_types are also updated.
     *
     * @return number of measurement_types records updated
     */
    @Transactional
    public int syncUnitLabels() {
        log.info("Starting unit label synchronization...");

        try {
            // Get all unit mappings
            List<UnitMapping> unitMappings = unitMappingRepository.findAll();
            Map<String, String> unitMap = unitMappings.stream()
                    .collect(Collectors.toMap(UnitMapping::getUnitCode, UnitMapping::getUnitLabel));

            // Get all measurement types
            List<MeasurementType> measurementTypes = measurementTypeRepository.findAll();

            int updatedCount = 0;

            // Update each measurement type with new unit label
            for (MeasurementType measurementType : measurementTypes) {
                if (measurementType.getUnit() != null && unitMap.containsKey(measurementType.getUnit())) {
                    String newUnitLabel = unitMap.get(measurementType.getUnit());
                    String oldUnitLabel = measurementType.getUnitLabel();

                    if (!newUnitLabel.equals(oldUnitLabel)) {
                        measurementType.setUnitLabel(newUnitLabel);
                        measurementTypeRepository.save(measurementType);
                        log.debug("Updated {} from '{}' to '{}'",
                                measurementType.getTypeId(), oldUnitLabel, newUnitLabel);
                        updatedCount++;
                    }
                }
            }

            log.info("Unit label synchronization completed. Updated {} measurement types", updatedCount);
            return updatedCount;

        } catch (Exception e) {
            log.error("Error during unit label synchronization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to synchronize unit labels: " + e.getMessage());
        }
    }
}


