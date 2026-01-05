package com.iot.soil.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum MeasurementProperty {
    SOIL_MOISTURE("Soil Moisture", 0),
    SOIL_TEMPERATURE("Soil Temperature", 1),
    SOIL_ELECTRICAL_CONDUCTIVITY("Soil Electrical Conductivity", 2),
    SOIL_PH("Soil PH", 3),
    SOIL_NITROGEN("Soil Nitrogen", 4),
    SOIL_PHOSPHORUS("Soil Phosphorus", 5),
    SOIL_POTASSIUM("Soil Potassium", 6);

    private final String displayName;
    private final int order;

    // Map za brzo pronalaženje po displayName
    private static final Map<String, MeasurementProperty> BY_DISPLAY_NAME =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            MeasurementProperty::getDisplayName,
                            mp -> mp
                    ));

    MeasurementProperty(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    public static int getOrderForProperty(String propertyName) {
        MeasurementProperty mp = BY_DISPLAY_NAME.get(propertyName);
        return mp != null ? mp.getOrder() : Integer.MAX_VALUE;
    }

    public static MeasurementProperty fromDisplayName(String displayName) {
        return BY_DISPLAY_NAME.get(displayName);
    }

    public static List<String> getDisplayNamesInOrder() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(MeasurementProperty::getOrder))
                .map(MeasurementProperty::getDisplayName)
                .collect(Collectors.toList());
    }
}