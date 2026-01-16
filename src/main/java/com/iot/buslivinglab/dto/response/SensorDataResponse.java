package com.iot.buslivinglab.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataResponse {
    private String sensorId;
    private String sensorName;
    private String location;
    private List<MeasurementData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeasurementData {
        private String property;
        private String unit;
        @JsonIgnore
        private Integer orderNumber;  // Za sortiranje iz baze
        private List<ValueData> values;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValueData {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private Instant time; // Promenjeno u Instant

        private Double value;
    }
}

