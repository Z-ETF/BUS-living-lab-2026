package com.iot.buslivinglab.controller;

import com.iot.buslivinglab.dto.request.SensorObservationRequest;
import com.iot.buslivinglab.dto.response.SensorDataResponse;
import com.iot.buslivinglab.service.SensorDataService;
import com.iot.buslivinglab.service.SensorQueryService;
import com.iot.buslivinglab.service.UnitSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensor Data Management", description = "BUS Living Lab 2026 - IoT soil sensor data management and retrieval APIs")
public class SensorDataController {

    private final SensorDataService sensorDataService;
    private final SensorQueryService sensorQueryService;
    private final UnitSyncService unitSyncService;

    @Operation(
            summary = "Receive sensor observation",
            description = "Process and store sensor observation data from field devices"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Observation saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/observations")
    public ResponseEntity<Map<String, Object>> receiveObservation(
            @Parameter(description = "Sensor observation data in SOSA/SSN format", required = true)
            @Valid @RequestBody SensorObservationRequest request) {

        log.info("Received observation request for sensor: {}",
                request.getMadeBySensor().getId());

        Map<String, Object> response = new HashMap<>();

        try {
            String result = sensorDataService.processSensorObservation(request);

            response.put("success", true);
            response.put("message", result);
            response.put("timestamp", java.time.Instant.now().toString()); // Uses UTC with Z suffix

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing observation: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", "Failed to process observation: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now().toString()); // Uses UTC with Z suffix

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
            summary = "Get latest single measurement per type",
            description = "Retrieve only the latest (single) measurement for each measurement type of a specific sensor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest measurements retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sensorId}/latest")
    public ResponseEntity<SensorDataResponse> getLatestSensorData(
            @PathVariable String sensorId) {

        log.info("Getting latest single measurement per type for sensor: {}", sensorId);

        try {
            SensorDataResponse data = sensorQueryService.getLatestSensorData(sensorId);
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error("Error retrieving latest data for sensor {}: {}", sensorId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Failed to get latest data: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get sensor data with flexible filtering options",
            description = """
                Retrieve sensor data with various filtering options:
                - By days: ?days=7 (default if no other filter specified)
                - By count: ?count=10 (latest N values per measurement type)
                - By date range: ?from=2025-01-01T00:00:00Z&to=2025-01-31T23:59:59Z
                
                Usage examples:
                - /api/sensor-data/sensor-7in1-001?days=7
                - /api/sensor-data/sensor-7in1-001?count=50
                - /api/sensor-data/sensor-7in1-001?from=2025-12-01T00:00:00Z&to=2025-12-31T23:59:59Z
                - /api/sensor-data/sensor-7in1-001 (defaults to days=7)
                
                Note: 'days' and 'count' parameters are mutually exclusive.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sensorId}")
    public ResponseEntity<SensorDataResponse> getSensorData(
            @Parameter(description = "ID of the sensor", example = "sensor-7in1-001", required = true)
            @PathVariable String sensorId,

            @Parameter(description = "Filter by last N days (mutually exclusive with 'count')",
                    example = "7")
            @RequestParam(required = false) Integer days,

            @Parameter(description = "Filter by last N values per measurement type (mutually exclusive with 'days')",
                    example = "10")
            @RequestParam(required = false) Integer count,

            @Parameter(description = "Filter by start date (ISO 8601 format)",
                    example = "2025-12-01T00:00:00Z")
            @RequestParam(required = false) String from,

            @Parameter(description = "Filter by end date (ISO 8601 format)",
                    example = "2025-12-31T23:59:59Z")
            @RequestParam(required = false) String to) {

        log.info("Getting data for sensor: {}, days: {}, count: {}, from: {}, to: {}",
                sensorId, days, count, from, to);

        try {
            // Validacija parametara
            validateParameters(days, count, from, to);

            SensorDataResponse data = sensorQueryService.getSensorDataFlexible(
                    sensorId, days, count, from, to);

            return ResponseEntity.ok(data);

        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for sensor {}: {}", sensorId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving data for sensor {}: {}", sensorId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Failed to get data: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Health check",
            description = "Check if the API is running"
    )
    @ApiResponse(responseCode = "200", description = "API is running")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Sensor Data API is running");
    }

    /**
     * Validacija query parametara
     */
    private void validateParameters(Integer days, Integer count, String from, String to) {
        // Validacija days vs count
        if (days != null && count != null) {
            throw new IllegalArgumentException(
                    "Cannot specify both 'days' and 'count' parameters. Choose one.");
        }

        // Validacija days
        if (days != null) {
            if (days <= 0) {
                throw new IllegalArgumentException("Days must be greater than 0");
            }
            if (days > 3650) { // 10 godina maksimum
                throw new IllegalArgumentException("Days cannot exceed 3650");
            }
        }

        // Validacija count
        if (count != null) {
            if (count <= 0) {
                throw new IllegalArgumentException("Count must be greater than 0");
            }
            if (count > 1000) {
                throw new IllegalArgumentException("Count cannot exceed 1000");
            }
        }

        // Validacija date range
        if ((from != null && to == null) || (from == null && to != null)) {
            throw new IllegalArgumentException(
                    "Both 'from' and 'to' parameters must be provided together for date range filtering");
        }
    }

    @Operation(
            summary = "Refresh unit mappings cache",
            description = "Reload unit mappings from database and sync all measurement_types. Use this after updating unit_label values in the unit_mappings table."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unit mappings refreshed and synchronized successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh-units")
    public ResponseEntity<Map<String, Object>> refreshUnitMappings() {
        log.info("Refreshing unit mappings from database and syncing measurement_types");

        Map<String, Object> response = new HashMap<>();

        try {
            // Refresh unit mappings cache in memory
            sensorDataService.refreshUnitMappings();

            // Sync all measurement_types with updated unit labels
            int updatedCount = unitSyncService.syncUnitLabels();

            response.put("success", true);
            response.put("message", "Unit mappings refreshed and " + updatedCount + " measurement types synchronized");
            response.put("updatedMeasurementTypes", updatedCount);
            response.put("timestamp", java.time.Instant.now().toString()); // Uses UTC with Z suffix

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error refreshing unit mappings: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", "Failed to refresh unit mappings: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now().toString()); // Uses UTC with Z suffix

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

