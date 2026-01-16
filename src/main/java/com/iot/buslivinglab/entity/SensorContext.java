package com.iot.buslivinglab.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
    private Instant createdAt; // Changed to Instant for UTC

    @PrePersist
    public void prePersist() {
        // Store in UTC timezone using Instant
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}


