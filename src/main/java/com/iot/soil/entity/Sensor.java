package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private LocalDateTime createdAt; // UKLONITE @CreationTimestamp

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // UKLONITE @UpdateTimestamp

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
        LocalDateTime now = LocalDateTime.now(belgradeZone);

        if (createdAt == null) {
            createdAt = now; // UTC+1
        }
        if (updatedAt == null) {
            updatedAt = now; // UTC+1
        }
    }

    @PreUpdate
    public void preUpdate() {
        ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
        updatedAt = LocalDateTime.now(belgradeZone); // UTC+1
    }
}