package com.iot.buslivinglab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.ZoneId;

@Entity
@Table(name = "sensor_data", indexes = {
        @Index(name = "idx_sensor_timestamp", columnList = "sensor_id, timestamp DESC"),
        @Index(name = "idx_timestamp", columnList = "timestamp DESC"),
        @Index(name = "idx_measurement_type", columnList = "measurement_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Long dataId;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "measurement_type", nullable = false)
    private String measurementType;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "unit", length = 100)
    private String unit;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp; // Ovo ostaje Instant za JSON timestamp

    @Column(name = "received_at")
    private Instant receivedAt; // Changed to Instant for proper UTC storage

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "raw_data", columnDefinition = "JSON")
    private String rawData;

    @PrePersist
    public void prePersist() {
        // Store in UTC timezone using Instant
        if (receivedAt == null) {
            receivedAt = Instant.now();
        }
    }
}

