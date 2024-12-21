package com.example.loader.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Converts a LocalDateTime to a formatted string based on the given pattern.
     *
     * @param timestamp the LocalDateTime to format
     * @param pattern   the format pattern (e.g., "yyyy-MM-dd HH:mm:ss")
     * @return the formatted date-time string
     */
    public static String convertLocalDateTimeToPattern(LocalDateTime timestamp, String pattern) {
        if (timestamp == null || pattern == null) {
            throw new IllegalArgumentException("Timestamp and pattern must not be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return timestamp.format(formatter);
    }

    /**
     * Converts a LocalDate to a formatted string based on the given pattern.
     *
     * @param date    the LocalDate to format
     * @param pattern the format pattern (e.g., "yyyy-MM-dd")
     * @return the formatted date string
     */
    public static String convertLocalDateToPattern(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            throw new IllegalArgumentException("Date and pattern must not be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}
