package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "measurement_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementType {

    @Id
    @Column(name = "type_id", length = 100)
    private String typeId;

    @Column(name = "display_name", length = 150, nullable = false)
    private String displayName;

    @Column(name = "unit", length = 100)
    private String unit;

    @Column(name = "unit_label", length = 50)
    private String unitLabel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "data_type", length = 20)
    private String dataType; // Promenjeno iz Enum u String

    @PrePersist
    @PreUpdate
    public void prepareForSave() {
        if (dataType == null) {
            dataType = "NUMERIC";
        }
    }
}