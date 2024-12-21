package com.example.loader.generators;

import com.example.loader.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class NormalizedNameGenerator {
    private final Map<String, Integer> generations = new HashMap<>();

    public String generateNormalizedName(String rawName) {
        String normalizedName = StringUtils.normalizeString(rawName);

        if (!generations.containsKey(normalizedName)) {
            generations.put(normalizedName, 0);
        }

        String result = setupGenerationName(normalizedName);

        increaseCountForGeneration(normalizedName);

        return result;
    }

    private void increaseCountForGeneration(String name) {
        int increasedCount = generations.get(name) + 1;
        generations.put(name, increasedCount);
    }

    private String setupGenerationName(String name) {
        int count = generations.get(name);

        if (count == 0)
            return name;

        return String.format("%s%d", name, count);
    }


}
