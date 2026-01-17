# Swagger UI Updates - BUS Living Lab 2026

## Overview
Updated Swagger/OpenAPI documentation to reflect the new application name and branding: **BUS Living Lab 2026 - Soil Sensor Data API**.

## Changes Made

### 1. OpenAPI Configuration (`OpenApiConfig.java`)
**File:** `src/main/java/com/iot/buslivinglab/config/OpenApiConfig.java`

Updated the OpenAPI configuration with:
- ✅ **API Title:** Changed from "Soil Sensor Data API" → "BUS Living Lab 2026 - Soil Sensor Data API"
- ✅ **API Version:** Updated to "1.0.0" (was "1.0")
- ✅ **Contact Name:** Changed to "BUS Living Lab 2026 Team" (was "Soil Sensor API")
- ✅ **Contact Email:** Updated to "support@buslivinglab.com" (was "support@example.com")
- ✅ **Contact URL:** Updated to "https://buslivinglab.com" (was "https://example.com")
- ✅ **API Description:** Enhanced with detailed project description:
  ```
  IoT Soil Sensor Data Management API for the BUS Living Lab 2026 project. 
  This API enables real-time collection, processing, and retrieval of sensor 
  observations from field devices using the SOSA/SSN standard format.
  ```

### 2. SensorDataController Swagger Tag
**File:** `src/main/java/com/iot/buslivinglab/controller/SensorDataController.java`

Updated the controller's Swagger tag:
- ✅ **Tag Name:** Changed from "Sensor Data" → "Sensor Data Management"
- ✅ **Tag Description:** Enhanced to "BUS Living Lab 2026 - IoT soil sensor data management and retrieval APIs"

### 3. Application Configuration (`application.properties`)
**File:** `src/main/resources/application.properties`

Updated application-level configuration:
- ✅ **Spring Application Name:** Changed from "soil-sensor-api" → "bus-living-lab-2026"
- ✅ **Log File Name:** Changed from "soil-sensor-api.log" → "bus-living-lab-2026.log"

### 4. Logback Configuration (`logback-spring.xml`)
**File:** `src/main/resources/logback-spring.xml`

Updated logging file names for consistency:
- ✅ **Main Log File:** Changed from "soil-sensor-api.log" → "bus-living-lab-2026.log"
- ✅ **Log Pattern:** Updated rolling pattern to "bus-living-lab-2026-%d{yyyy-MM-dd}.%i.log.gz"

## Impact on Swagger UI

### Before Update
```
Title: Soil Sensor Data API
Version: 1.0
Contact: Soil Sensor API (support@example.com)
Description: This API exposes endpoints for managing soil sensor data.
```

### After Update
```
Title: BUS Living Lab 2026 - Soil Sensor Data API
Version: 1.0.0
Contact: BUS Living Lab 2026 Team (support@buslivinglab.com)
Description: IoT Soil Sensor Data Management API for the BUS Living Lab 2026 project. 
             This API enables real-time collection, processing, and retrieval of sensor 
             observations from field devices using the SOSA/SSN standard format.
```

## Accessing Updated Swagger UI

Once the application starts, you can access the updated Swagger UI at:

- **Swagger UI:** `https://localhost:8443/swagger-ui.html`
- **OpenAPI JSON:** `https://localhost:8443/v3/api-docs`
- **OpenAPI YAML:** `https://localhost:8443/v3/api-docs.yaml`

## Logging Improvements

All logs are now stored with the new application name:
- **Main Log:** `logs/bus-living-lab-2026.log`
- **Daily Archives:** `logs/bus-living-lab-2026-YYYY-MM-DD.N.log.gz`
- **Max History:** 30 days
- **Max File Size:** 10 MB per file

## Version Information

- **Application Version:** 1.0.0
- **Java Version:** 21
- **Spring Boot:** 3.2.0
- **OpenAPI/Swagger:** 3.0

## Commit Information

- **Commit Hash:** 0009194
- **Commit Message:** "feat: Update Swagger UI with BUS Living Lab 2026 branding and improved documentation"
- **Files Changed:** 4
- **Insertions:** 13
- **Deletions:** 11

## Testing

To verify the changes:

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Start the application:
   ```bash
   java -jar target/bus-living-lab2026-1.0.0.jar
   ```

3. Navigate to: `https://localhost:8443/swagger-ui.html`

4. Verify that:
   - ✅ Title shows "BUS Living Lab 2026 - Soil Sensor Data API"
   - ✅ Version shows "1.0.0"
   - ✅ Contact information is updated
   - ✅ API description is detailed and informative

## Notes

- All endpoints remain the same; only documentation has been updated
- No breaking changes to the API
- Log files will now use the new naming convention
- Application name is used in Spring Boot actuator endpoints if enabled

---

**Last Updated:** January 17, 2026  
**Updated By:** GitHub Copilot

