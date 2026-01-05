package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @Column(name = "last_observed")
    private LocalDateTime lastObserved; // Promenjeno u LocalDateTime

    @Column(name = "min_threshold")
    private Double minThreshold;

    @Column(name = "max_threshold")
    private Double maxThreshold;

    @PrePersist
    @PreUpdate
    public void updateLastObserved() {
        // Ovo će se pozvati kada se ručno setuje last_observed
        // Ali mi ga setujemo u service-u
    }

    public String getSensorId() {
        return id != null ? id.getSensorId() : null;
    }

    public String getMeasurementType() {
        return id != null ? id.getMeasurementType() : null;
    }
}