package com.iot.soil.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorObservationRequest {

    @JsonProperty("@context")
    private Context context;

    @JsonProperty("sosa:madeBySensor")
    private SensorInfo madeBySensor;

    @JsonProperty("sosa:hasFeatureOfInterest")
    private FeatureOfInterest hasFeatureOfInterest;

    @JsonProperty("sosa:hasMember")
    private List<Observation> hasMember;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Context {
        private String sosa;
        private String ssn;
        private String qudt;
        private String unit;
        private String xsd;
        private String rdfs;
        private String ll;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SensorInfo {
        @JsonProperty("@id")
        private String id;

        @JsonProperty("@type")
        private String type;

        @JsonProperty("rdfs:label")
        private String label;

        @JsonProperty("sosa:observes")
        private List<Property> observes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Property {
        @JsonProperty("@id")
        private String id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeatureOfInterest {
        @JsonProperty("@type")
        private String type;

        @JsonProperty("rdfs:label")
        private String label;

        @JsonProperty("ll:location")
        private String location;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Observation {
        @JsonProperty("@type")
        private String type;

        @JsonProperty("sosa:observedProperty")
        private ObservedProperty observedProperty;

        @JsonProperty("sosa:phenomenonTime")
        private String phenomenonTime;

        @JsonProperty("sosa:hasResult")
        private Result hasResult;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObservedProperty {
        @JsonProperty("@id")
        private String id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("qudt:numericValue")
        private Double numericValue;

        @JsonProperty("qudt:unit")
        private String unit;
    }
}
