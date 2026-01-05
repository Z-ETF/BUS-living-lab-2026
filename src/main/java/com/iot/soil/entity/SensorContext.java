package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "sensor_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "context_id")
    private Long contextId;

    @Column(name = "sensor_id", nullable = false, length = 100)
    private String sensorId;

    @Column(name = "context_name", nullable = false, length = 100)
    private String contextName;

    @Column(name = "context_uri", nullable = false, columnDefinition = "TEXT")
    private String contextUri;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        ZoneId belgradeZone = ZoneId.of("Europe/Belgrade");
        if (createdAt == null) {
            createdAt = LocalDateTime.now(belgradeZone);
        }
    }
}

