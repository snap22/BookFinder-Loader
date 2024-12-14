package com.example.loader.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * DTO for Book model in JSON format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookJson {
    @JsonProperty("name")
    private String name;

    @JsonProperty("author")
    private String author;
}
