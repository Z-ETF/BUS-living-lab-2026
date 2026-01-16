package com.iot.buslivinglab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "sensors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @Column(name = "sensor_id", length = 100)
    private String sensorId;

    @Column(name = "sensor_name", length = 150, nullable = false)
    private String sensorName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "created_at")
    private Instant createdAt; // Changed to Instant for UTC

    @Column(name = "updated_at")
    private Instant updatedAt; // Changed to Instant for UTC

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

