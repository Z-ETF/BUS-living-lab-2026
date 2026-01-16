package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "sensor_measurement_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorMeasurementType {

    @EmbeddedId
    private SensorMeasurementTypeId id;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_observed", columnDefinition = "TIMESTAMP(6)")
    private Instant lastObserved; // Instant for proper UTC storage

    @Column(name = "min_threshold")
    private Double minThreshold;

    @Column(name = "max_threshold")
    private Double maxThreshold;


    public String getSensorId() {
        return id != null ? id.getSensorId() : null;
    }

    public String getMeasurementType() {
        return id != null ? id.getMeasurementType() : null;
    }
}
