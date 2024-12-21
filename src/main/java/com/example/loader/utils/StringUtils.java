package com.example.loader.utils;

import java.text.Normalizer;
import java.util.Locale;

public class StringUtils {
    /**
     * Normalizes a string for use in a URL.
     *
     * - Replaces spaces with dashes
     * - Removes accent symbols (e.g., č -> c)
     * - Removes problematic characters for URLs
     * - Converts to lowercase
     *
     * @param string the input string to normalize
     * @return the normalized, URL-safe string
     */
    public static String normalizeString(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Input string must not be null");
        }

        // Normalize the string to remove accents
        String normalized = Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // Removes accent marks

        // Replace spaces with dashes
        normalized = normalized.replaceAll("\\s+", "-");

        // Remove problematic characters (e.g., slashes, special symbols)
        normalized = normalized.replaceAll("[^a-zA-Z0-9-]", "");

        // Convert to lowercase
        return normalized.toLowerCase(Locale.ROOT);
    }
}
