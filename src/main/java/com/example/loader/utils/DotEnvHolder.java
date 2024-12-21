package com.example.loader.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class DotEnvHolder {
    Dotenv dotenv = Dotenv.load();

    public String getVariable(String key) {
        if (dotenv.get(key) == null) {
            throw new RuntimeException("Environment variable not found: " + key);
        }

        return dotenv.get(key);
    }
}
