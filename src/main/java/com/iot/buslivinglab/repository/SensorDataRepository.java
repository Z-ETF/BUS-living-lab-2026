package com.iot.soil.repository;

import com.iot.buslivinglab.entity.SensorData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findBySensorIdOrderByTimestampDesc(String sensorId);

    @Query("SELECT sd FROM SensorData sd WHERE sd.sensorId = :sensorId " +
            "AND sd.measurementType = :measurementType " +
            "AND sd.timestamp >= :startDate " +
            "ORDER BY sd.timestamp DESC")
    List<SensorData> findRecentData(
            @Param("sensorId") String sensorId,
            @Param("measurementType") String measurementType,
            @Param("startDate") Instant startDate);

    @Query("SELECT sd FROM SensorData sd WHERE sd.sensorId = :sensorId " +
            "AND sd.timestamp >= :startTime AND sd.timestamp <= :endTime " +
            "ORDER BY sd.timestamp DESC")
    List<SensorData> findBySensorAndTimeRange(
            @Param("sensorId") String sensorId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    // OPTIMIZED: Get latest measurement for each type using efficient query
    @Query(value = "SELECT DISTINCT sd.* FROM sensor_data sd " +
            "INNER JOIN (" +
            "  SELECT measurement_type, MAX(timestamp) as max_timestamp " +
            "  FROM sensor_data " +
            "  WHERE sensor_id = :sensorId " +
            "  GROUP BY measurement_type" +
            ") latest ON sd.measurement_type = latest.measurement_type " +
            "AND sd.timestamp = latest.max_timestamp " +
            "AND sd.sensor_id = :sensorId " +
            "ORDER BY sd.measurement_type", nativeQuery = true)
    List<SensorData> findLatestMeasurements(@Param("sensorId") String sensorId);


    // Pronađi različite tipove merenja za senzor
    @Query("SELECT DISTINCT sd.measurementType FROM SensorData sd WHERE sd.sensorId = :sensorId")
    List<String> findDistinctMeasurementTypesBySensorId(@Param("sensorId") String sensorId);

    // Poslednjih N merenja za konkretan tip merenja
    @Query("SELECT sd FROM SensorData sd " +
            "WHERE sd.sensorId = :sensorId AND sd.measurementType = :measurementType " +
            "ORDER BY sd.timestamp DESC")
    List<SensorData> findTopNBySensorIdAndMeasurementType(
            @Param("sensorId") String sensorId,
            @Param("measurementType") String measurementType,
            Pageable pageable);

    // Helper metoda za jednostavno pozivanje
    default List<SensorData> findLatestNBySensorIdAndMeasurementType(
            String sensorId, String measurementType, int n) {
        Pageable pageable = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "timestamp"));
        return findTopNBySensorIdAndMeasurementType(sensorId, measurementType, pageable);
    }

    // Podaci po datumu
    @Query("SELECT sd FROM SensorData sd " +
            "WHERE sd.sensorId = :sensorId " +
            "AND sd.timestamp BETWEEN :from AND :to " +
            "ORDER BY sd.timestamp DESC")
    List<SensorData> findBySensorIdAndTimestampBetweenOrderByTimestampDesc(
            @Param("sensorId") String sensorId,
            @Param("from") Instant from,
            @Param("to") Instant to);

    // Najnoviji podatak za senzor
    Optional<SensorData> findTopBySensorIdOrderByTimestampDesc(String sensorId);
}
