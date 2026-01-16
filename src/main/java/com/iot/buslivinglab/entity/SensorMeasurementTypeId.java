package com.iot.soil.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorMeasurementTypeId implements Serializable {
    private String sensorId;
    private String measurementType;
}
