package com.iot.soil.service;

import com.iot.buslivinglab.dto.response.SensorDataResponse;
import com.iot.buslivinglab.entity.MeasurementType;
import com.iot.buslivinglab.entity.Sensor;
import com.iot.buslivinglab.entity.SensorData;
import com.iot.buslivinglab.repository.MeasurementTypeRepository;
import com.iot.buslivinglab.repository.SensorDataRepository;
import com.iot.buslivinglab.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorQueryService {

    private final SensorDataRepository sensorDataRepository;
    private final MeasurementTypeRepository measurementTypeRepository;
    private final SensorRepository sensorRepository;

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    // ========== PRIVATNE POMOĆNE METODE ==========

    /**
     * Sortira listu MeasurementData prema redosledu iz baze (order_number iz measurement_types)
     */
    private void sortMeasurementData(List<SensorDataResponse.MeasurementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        dataList.sort(Comparator.comparingInt(data -> {
            // Ako nema orderNumber, koristi default vrednost
            return data.getOrderNumber() != null ? data.getOrderNumber() : Integer.MAX_VALUE;
        }));
    }

    /**
     * Kreira MeasurementData objekt iz liste SensorData
     */
    private Optional<SensorDataResponse.MeasurementData> createMeasurementData(
            String measurementTypeId,
            List<SensorData> sensorDataList) {

        Optional<MeasurementType> measurementTypeOpt = measurementTypeRepository.findById(measurementTypeId);

        if (measurementTypeOpt.isEmpty() || sensorDataList.isEmpty()) {
            return Optional.empty();
        }

        MeasurementType measurementType = measurementTypeOpt.get();

        // Sortiraj od najnovijeg ka najstarijem
        sensorDataList.sort(Comparator.comparing(SensorData::getTimestamp).reversed());

        // Konvertuj SensorData u ValueData
        List<SensorDataResponse.ValueData> valueDataList = sensorDataList.stream()
                .map(data -> SensorDataResponse.ValueData.builder()
                        .time(data.getTimestamp())
                        .value(data.getValue())
                        .build())
                .collect(Collectors.toList());

        // Kreiraj MeasurementData
        SensorDataResponse.MeasurementData measurementData = SensorDataResponse.MeasurementData.builder()
                .property(measurementType.getDisplayName())
                .unit(measurementType.getUnitLabel() != null ?
                        measurementType.getUnitLabel() : "")
                .orderNumber(measurementType.getOrderNumber())
                .values(valueDataList)
                .build();

        return Optional.of(measurementData);
    }

    /**
     * Kreira MeasurementData sa samo jednim (najnovijim) merenjem
     */
    private Optional<SensorDataResponse.MeasurementData> createLatestMeasurementData(
            String measurementTypeId,
            List<SensorData> sensorDataList) {

        if (sensorDataList.isEmpty()) {
            return Optional.empty();
        }

        Optional<MeasurementType> measurementTypeOpt = measurementTypeRepository.findById(measurementTypeId);

        if (measurementTypeOpt.isEmpty()) {
            return Optional.empty();
        }

        // Uzmi najnoviji (prvi u listi koja je već sortirana po timestamp desc)
        SensorData latestData = sensorDataList.get(0);

        // Kreiraj listu sa samo jednim merenjem
        List<SensorDataResponse.ValueData> valueDataList = Collections.singletonList(
                SensorDataResponse.ValueData.builder()
                        .time(latestData.getTimestamp())
                        .value(latestData.getValue())
                        .build()
        );

        MeasurementType measurementType = measurementTypeOpt.get();

        // Kreiraj MeasurementData
        SensorDataResponse.MeasurementData measurementData = SensorDataResponse.MeasurementData.builder()
                .property(measurementType.getDisplayName())
                .unit(measurementType.getUnitLabel() != null ?
                        measurementType.getUnitLabel() : "")
                .orderNumber(measurementType.getOrderNumber())
                .values(valueDataList)
                .build();

        return Optional.of(measurementData);
    }

    /**
     * Kreira SensorDataResponse sa sortiranim podacima
     */
    private SensorDataResponse buildSensorDataResponse(
            String sensorId,
            List<SensorDataResponse.MeasurementData> measurementDataList,
            Optional<String> location) {

        // Sortiraj podatke po definisanom redosledu
        sortMeasurementData(measurementDataList);

        // Dobij ime senzora iz baze
        String sensorName = sensorRepository.findById(sensorId)
                .map(Sensor::getSensorName)
                .orElse("Unknown Sensor");

        // Kreiraj response
        return SensorDataResponse.builder()
                .sensorId(sensorId)
                .sensorName(sensorName)
                .location(location.orElse("Unknown Location"))
                .data(measurementDataList)
                .build();
    }

    /**
     * Parsira ISO string u Instant
     */
    private Instant parseIsoTimestamp(String isoString) {
        if (isoString == null || isoString.trim().isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(isoString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid ISO timestamp format: " + isoString, e);
        }
    }

    /**
     * Dobija lokaciju iz najnovijeg podatka senzora
     */
    private String getSensorLocation(String sensorId) {
        return sensorDataRepository
                .findTopBySensorIdOrderByTimestampDesc(sensorId)
                .map(SensorData::getLocation)
                .orElse("Unknown Location");
    }

    // ========== JAVNE METODE ==========

    /**
     * Nova fleksibilna metoda koja podržava sve filtere
     */
    public SensorDataResponse getSensorDataFlexible(
            String sensorId,
            Integer days,
            Integer count,
            String from,
            String to) {

        // Ako je definisan count - poslednjih N vrijednosti po tipu
        if (count != null) {
            return getLatestNValues(sensorId, count);
        }

        // Ako je definisan date range
        if (from != null && to != null) {
            return getSensorDataByDateRange(sensorId, from, to);
        }

        // Ako je definisan days ili default (7 dana)
        int daysToUse = (days != null) ? days : 7;
        return getSensorData(sensorId, daysToUse);
    }

    /**
     * Metoda za sva merenja u periodu (po danima)
     */
    public SensorDataResponse getSensorData(String sensorId, Integer days) {
        // Odredi početni datum
        LocalDateTime startDate = (days != null) ?
                LocalDateTime.now(ZoneId.of("Europe/Belgrade")).minusDays(days) :
                LocalDateTime.now(ZoneId.of("Europe/Belgrade")).minusDays(7);

        // Dobij sva merenja za senzor
        List<SensorData> allData = sensorDataRepository.findBySensorIdOrderByTimestampDesc(sensorId);

        // Filtriraj po datumu
        List<SensorData> filteredData = allData.stream()
                .filter(data -> data.getTimestamp().isAfter(
                        startDate.atZone(ZoneId.of("Europe/Belgrade")).toInstant()))
                .collect(Collectors.toList());

        // Grupiši po tipu merenja
        Map<String, List<SensorData>> groupedByType = filteredData.stream()
                .collect(Collectors.groupingBy(SensorData::getMeasurementType));

        List<SensorDataResponse.MeasurementData> measurementDataList = new ArrayList<>();

        for (Map.Entry<String, List<SensorData>> entry : groupedByType.entrySet()) {
            String measurementTypeId = entry.getKey();
            List<SensorData> dataList = entry.getValue();

            createMeasurementData(measurementTypeId, dataList)
                    .ifPresent(measurementDataList::add);
        }

        // Dobij lokaciju (prvo merenje u listi)
        Optional<String> location = filteredData.isEmpty() ?
                Optional.empty() :
                Optional.of(filteredData.get(0).getLocation());

        // Kreiraj i vrati response
        return buildSensorDataResponse(sensorId, measurementDataList, location);
    }

    /**
     * Metoda za podatke po datumu
     */
    public SensorDataResponse getSensorDataByDateRange(String sensorId, String from, String to) {
        // Parsiraj datume
        Instant fromInstant = parseIsoTimestamp(from);
        Instant toInstant = parseIsoTimestamp(to);

        if (fromInstant == null || toInstant == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' timestamps must be provided");
        }

        if (fromInstant.isAfter(toInstant)) {
            throw new IllegalArgumentException("'from' date must be before 'to' date");
        }

        // Dobij podatke za period
        List<SensorData> filteredData = sensorDataRepository
                .findBySensorIdAndTimestampBetweenOrderByTimestampDesc(sensorId, fromInstant, toInstant);

        // Grupiši po tipu merenja
        Map<String, List<SensorData>> groupedByType = filteredData.stream()
                .collect(Collectors.groupingBy(SensorData::getMeasurementType));

        List<SensorDataResponse.MeasurementData> measurementDataList = new ArrayList<>();

        for (Map.Entry<String, List<SensorData>> entry : groupedByType.entrySet()) {
            String measurementTypeId = entry.getKey();
            List<SensorData> dataList = entry.getValue();

            createMeasurementData(measurementTypeId, dataList)
                    .ifPresent(measurementDataList::add);
        }

        // Dobij lokaciju (prvo merenje u listi)
        Optional<String> location = filteredData.isEmpty() ?
                Optional.empty() :
                Optional.of(filteredData.get(0).getLocation());

        // Kreiraj i vrati response
        return buildSensorDataResponse(sensorId, measurementDataList, location);
    }

    /**
     * Metoda za samo poslednja merenja (jedno po tipu)
     */
    public SensorDataResponse getLatestSensorData(String sensorId) {
        // Dobij poslednja merenja za svaki tip
        List<SensorData> latestMeasurements = sensorDataRepository.findLatestMeasurements(sensorId);

        if (latestMeasurements.isEmpty()) {
            throw new RuntimeException("No data found for sensor: " + sensorId);
        }

        // Grupiši po tipu merenja
        Map<String, List<SensorData>> groupedByType = latestMeasurements.stream()
                .collect(Collectors.groupingBy(SensorData::getMeasurementType));

        List<SensorDataResponse.MeasurementData> measurementDataList = new ArrayList<>();

        for (Map.Entry<String, List<SensorData>> entry : groupedByType.entrySet()) {
            String measurementTypeId = entry.getKey();
            List<SensorData> dataList = entry.getValue();

            createLatestMeasurementData(measurementTypeId, dataList)
                    .ifPresent(measurementDataList::add);
        }

        // Dobij lokaciju
        Optional<String> location = Optional.of(latestMeasurements.get(0).getLocation());

        // Kreiraj i vrati response
        return buildSensorDataResponse(sensorId, measurementDataList, location);
    }

    /**
     * Metoda za poslednjih N vrednosti po tipu merenja
     */
    public SensorDataResponse getLatestNValues(String sensorId, Integer count) {
        if (count == null || count <= 0) {
            count = 10; // Default vrednost
        }

        // Limit za bezbjednost
        if (count > 1000) {
            count = 1000;
        }

        // 1. Pronađi sve tipove merenja koje ovaj senzor ima
        List<String> measurementTypeIds = sensorDataRepository
                .findDistinctMeasurementTypesBySensorId(sensorId);

        if (measurementTypeIds.isEmpty()) {
            throw new RuntimeException("No measurement types found for sensor: " + sensorId);
        }

        List<SensorDataResponse.MeasurementData> measurementDataList = new ArrayList<>();

        // 2. Za svaki tip merenja, uzmi poslednjih N vrijednosti
        for (String measurementTypeId : measurementTypeIds) {
            Optional<MeasurementType> measurementTypeOpt = measurementTypeRepository
                    .findById(measurementTypeId);

            if (measurementTypeOpt.isPresent()) {
                // Uzmi poslednjih N vrijednosti za ovaj tip merenja
                List<SensorData> dataList = sensorDataRepository
                        .findLatestNBySensorIdAndMeasurementType(sensorId, measurementTypeId, count);

                if (!dataList.isEmpty()) {
                    createMeasurementData(measurementTypeId, dataList)
                            .ifPresent(measurementDataList::add);
                }
            }
        }

        // 3. Dobij lokaciju i senzor info
        String location = getSensorLocation(sensorId);

        // 4. Kreiraj response
        return buildSensorDataResponse(sensorId, measurementDataList, Optional.of(location));
    }
}
