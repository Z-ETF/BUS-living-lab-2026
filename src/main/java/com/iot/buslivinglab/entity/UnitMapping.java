package com.iot.soil.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit_mappings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitMapping {

    @Id
    @Column(name = "unit_code")
    private String unitCode;  // npr. "unit:PERCENT"

    @Column(name = "unit_label", nullable = false)
    private String unitLabel;  // npr. "%"

    @Column(name = "description")
    private String description;  // opis za informaciju

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

