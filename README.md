# Living Lab 2026 - Soil Sensor Data API

A modern Spring Boot REST API for managing and querying IoT soil sensor data from the Living Lab project. This application provides endpoints for receiving sensor observations, retrieving measurement data, and managing sensor configurations.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
  - [API Endpoints](#api-endpoints)
  - [Example Requests](#example-requests)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Development](#development)
- [Building & Deployment](#building--deployment)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## 📖 Overview

Living Lab 2026 is a comprehensive IoT sensor data platform designed to collect, process, and manage soil sensor observations from field devices. The API follows the **SOSA (Sensor, Observation, Sample, and Actuator)** and **SSN (Semantic Sensor Network)** standards for sensor data interoperability.

**Project Name:** bus-living-lab2026  
**Version:** 1.0.0  
**Java Version:** 21  
**Framework:** Spring Boot 3.2.0

## ✨ Features

- ✅ **RESTful API** for sensor data management
- ✅ **SOSA/SSN Compliant** sensor observation format
- ✅ **Real-time Data Processing** from field sensors
- ✅ **Flexible Querying** with multiple filtering options
- ✅ **Unit Conversion** and synchronization
- ✅ **Measurement Type Management**
- ✅ **API Documentation** with Swagger/OpenAPI 3.0
- ✅ **Database Connection Pooling** (Hikari)
- ✅ **UTC Timezone Support** across all operations
- ✅ **Comprehensive Logging** with daily log rotation
- ✅ **SSL/TLS Support** for secure connections
- ✅ **Input Validation** with Jakarta Validation

## 🔧 Prerequisites

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper `mvnw`)
- **MySQL 8.0+** (or compatible database)
- **Git**

## 📦 Installation

### Clone the Repository

```bash
git clone https://github.com/yourusername/bus-living-lab2026.git
cd bus-living-lab2026
```

### Install Dependencies

Using Maven:

```bash
./mvnw clean install
```

Or with Maven installed:

```bash
mvn clean install
```

## ⚙️ Configuration

### Database Configuration

Edit `src/main/resources/application.properties` to configure your database connection:

```properties
# Database Connection
spring.datasource.url=jdbc:mysql://YOUR_HOST:3306/livinglab?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Connection Pool Settings

Adjust Hikari connection pool for your environment:

```properties
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### Server Configuration

Set server port and SSL settings:

```properties
# Server Port
server.port=8443

# SSL Configuration (for production)
server.ssl.enabled=true
server.ssl.key-store=file:/opt/ssl/keystore.p12
server.ssl.key-store-password=your_password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=springboot
```

### Logging Configuration

Logs are configured in `src/main/resources/logback-spring.xml`:

```properties
logging.level.root=INFO
logging.level.com.iot.buslivinglab=INFO
logging.level.org.springframework.web=DEBUG
logging.file.name=logs/soil-sensor-api.log
logging.file.max-history=30
logging.file.max-size=10MB
```

## 🚀 Usage

### Running the Application

```bash
./mvnw spring-boot:run
```

Or after building:

```bash
java -jar target/bus-living-lab2026-1.0.0.jar
```

The application will start on `https://localhost:8443` (if SSL is enabled) or `http://localhost:9000` (if disabled).

### API Documentation

Once running, access the interactive API documentation:

- **Swagger UI:** `https://localhost:8443/swagger-ui.html`
- **OpenAPI JSON:** `https://localhost:8443/v3/api-docs`
- **OpenAPI YAML:** `https://localhost:8443/v3/api-docs.yaml`

### API Endpoints

#### Receive Sensor Observation

**Endpoint:** `POST /api/sensor-data/observations`

Process and store sensor observation data from field devices in SOSA/SSN format.

**Request Body:**

```json
{
  "madeBySensor": {
    "id": "sensor_001"
  },
  "hasResult": {
    "hasValue": "25.5"
  },
  "observedProperty": "temperature",
  "resultTime": "2024-01-16T10:30:00Z",
  "phenomenonTime": "2024-01-16T10:30:00Z"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Observation saved successfully",
  "timestamp": "2024-01-16T10:30:00Z"
}
```

#### Get Latest Sensor Data

**Endpoint:** `GET /api/sensor-data/{sensorId}/latest`

Retrieve the latest (single) measurement for each measurement type of a specific sensor.

**Response:**

```json
{
  "sensorId": "sensor_001",
  "measurements": [
    {
      "type": "temperature",
      "value": "25.5",
      "unit": "°C",
      "timestamp": "2024-01-16T10:30:00Z"
    }
  ]
}
```

#### Get Sensor Data with Filtering

**Endpoint:** `GET /api/sensor-data/{sensorId}`

Retrieve sensor data with flexible filtering options (time range, measurement type, etc.).

**Query Parameters:**

- `startTime` - Start timestamp (ISO 8601 format)
- `endTime` - End timestamp (ISO 8601 format)
- `measurementType` - Filter by measurement type
- `limit` - Maximum number of results
- `offset` - Pagination offset

**Example:**

```
GET /api/sensor-data/sensor_001?startTime=2024-01-01T00:00:00Z&endTime=2024-01-31T23:59:59Z&limit=100
```

### Example Requests

#### Using cURL

```bash
# Send sensor observation
curl -X POST https://localhost:8443/api/sensor-data/observations \
  -H "Content-Type: application/json" \
  -d '{
    "madeBySensor": {"id": "sensor_001"},
    "hasResult": {"hasValue": "25.5"},
    "observedProperty": "temperature",
    "resultTime": "2024-01-16T10:30:00Z",
    "phenomenonTime": "2024-01-16T10:30:00Z"
  }'

# Get latest data
curl https://localhost:8443/api/sensor-data/sensor_001/latest
```

#### Using Postman

1. Create a new POST request to `https://localhost:8443/api/sensor-data/observations`
2. Set the request body to JSON format
3. Paste the observation data
4. Click Send

#### Using Python

```python
import requests
import json

url = "https://localhost:8443/api/sensor-data/observations"
payload = {
    "madeBySensor": {"id": "sensor_001"},
    "hasResult": {"hasValue": "25.5"},
    "observedProperty": "temperature",
    "resultTime": "2024-01-16T10:30:00Z",
    "phenomenonTime": "2024-01-16T10:30:00Z"
}

response = requests.post(url, json=payload, verify=False)
print(response.json())
```

## 🏗️ Architecture

### Project Structure

```
bus-living-lab2026/
├── src/
│   ├── main/
│   │   ├── java/com/iot/buslivinglab/
│   │   │   ├── BusLivingLabApplication.java    # Main Spring Boot application
│   │   │   ├── config/                         # Configuration classes (OpenAPI, etc.)
│   │   │   ├── controller/                     # REST controllers
│   │   │   ├── service/                        # Business logic
│   │   │   ├── repository/                     # JPA repositories
│   │   │   ├── entity/                         # JPA entity classes
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── request/                    # Request DTOs
│   │   │   │   └── response/                   # Response DTOs
│   │   │   ├── enums/                          # Enumeration classes
│   │   │   └── exception/                      # Exception handling
│   │   └── resources/
│   │       ├── application.properties          # Application configuration
│   │       └── logback-spring.xml              # Logging configuration
│   └── test/
│       └── java/...                            # Unit tests
├── docs/                                        # API documentation files
├── pom.xml                                      # Maven configuration
├── mvnw                                         # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                     # Maven wrapper (Windows)
└── README.md                                    # This file
```

### Core Components

| Component | Purpose |
|-----------|---------|
| **Controller** | Handles HTTP requests and responses |
| **Service** | Contains business logic for data processing |
| **Repository** | Manages database operations via JPA |
| **Entity** | Represents database tables |
| **DTO** | Data transfer objects for API communication |
| **Exception Handler** | Global exception handling |
| **Config** | Application-level configurations |

### Key Entities

- **Sensor** - IoT sensor device information
- **SensorData** - Individual sensor measurements
- **MeasurementType** - Types of measurements (temperature, humidity, etc.)
- **SensorMeasurementType** - Mapping between sensors and measurement types
- **UnitMapping** - Unit conversions and standardization
- **SensorContext** - Contextual information about sensor deployments

## 🗄️ Database Schema

The application uses MySQL with the following main tables:

- `sensors` - Sensor device registry
- `sensor_data` - Time-series sensor observations
- `measurement_types` - Available measurement types
- `sensor_measurement_types` - Sensor-to-measurement mappings
- `unit_mappings` - Unit conversion rules
- `sensor_contexts` - Deployment contexts

All timestamps are stored in **UTC timezone** with timezone-aware handling.

## 👨‍💻 Development

### Building for Development

```bash
./mvnw clean install
```

### Running Tests

```bash
./mvnw test
```

### Debugging

1. Add debugging flag to startup:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
   ```

2. Connect your IDE debugger to `localhost:5005`

### Code Quality

- Uses **Lombok** for reducing boilerplate code
- Follows **Spring Boot best practices**
- Comprehensive **input validation** with Jakarta Validation
- Structured **exception handling**
- **Detailed logging** for troubleshooting

## 🔨 Building & Deployment

### Build JAR

```bash
./mvnw clean package
```

Output: `target/bus-living-lab2026-1.0.0.jar`

### Build Docker Image (Optional)

```bash
./mvnw spring-boot:build-image
```

### Deploy to Server

1. Copy JAR to server:
   ```bash
   scp target/bus-living-lab2026-1.0.0.jar user@server:/opt/apps/
   ```

2. Create SSL keystore on server:
   ```bash
   keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 -keystore keystore.p12 -storetype PKCS12
   ```

3. Run the application:
   ```bash
   java -jar bus-living-lab2026-1.0.0.jar
   ```

## 🐛 Troubleshooting

### Database Connection Issues

**Problem:** "Failed to connect to database"

**Solution:**
- Verify MySQL server is running
- Check database credentials in `application.properties`
- Ensure database user has correct permissions
- Verify network connectivity to database host

### SSL Certificate Errors

**Problem:** "javax.net.ssl.SSLException"

**Solution:**
- Verify keystore file path is correct
- Check keystore password matches configuration
- Ensure certificate is valid and not expired
- Try disabling SSL for local development by setting `server.ssl.enabled=false`

### Port Already in Use

**Problem:** "Address already in use: 8443"

**Solution:**
- Kill the process using the port
- Or change port in `application.properties`: `server.port=8444`

### Slow Queries

**Problem:** API responses are slow

**Solution:**
- Check database connection pool settings
- Verify database indexes are created
- Review logs for slow query warnings
- Increase `spring.datasource.hikari.maximum-pool-size`

### Timezone Issues

**Problem:** Timestamps are incorrect or in wrong timezone

**Solution:**
- Verify `serverTimezone=UTC` in database URL
- Check `spring.jpa.properties.hibernate.jdbc.time_zone=UTC`
- Ensure system timezone is correctly set
- Check client application is sending UTC timestamps

## 📝 License

This project is part of the Living Lab 2026 initiative. Please refer to the LICENSE file in the repository for details.

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For issues, questions, or suggestions:

- Open an issue on GitHub
- Contact the development team
- Check the documentation in `docs/` directory

## 🔗 References

- [SOSA/SSN Ontology](https://www.w3.org/TR/vocab-ssn/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

**Last Updated:** January 16, 2026  
**Version:** 1.0.0

