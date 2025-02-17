package com.beeja.api.projectmanagement.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum LogHourEnum {
    HALF_HOUR("00:30"),
    ONE_HOUR("01:00"),
    ONE_HALF_HOUR("01:30"),
    TWO_HOURS("02:00"),
    TWO_HALF_HOURS("02:30"),
    THREE_HOURS("03:00"),
    THREE_HALF_HOURS("03:30"),
    FOUR_HOURS("04:00"),
    FOUR_HALF_HOURS("04:30"),
    FIVE_HOURS("05:00"),
    FIVE_HALF_HOURS("05:30"),
    SIX_HOURS("06:00"),
    SIX_HALF_HOURS("06:30"),
    SEVEN_HOURS("07:00"),
    SEVEN_HALF_HOURS("07:30"),
    EIGHT_HOURS("08:00");

    private final String value;

    LogHourEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static boolean isValid(String logHour) {
        return Arrays.stream(LogHourEnum.values())
                .anyMatch(hour -> hour.value.equals(logHour));
    }
    public static String getAllowedValues() {
        return Arrays.stream(LogHourEnum.values())
                .map(LogHourEnum::getValue)
                .collect(Collectors.joining(", "));
    }
}
