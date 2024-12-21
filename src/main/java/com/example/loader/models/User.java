package com.example.loader.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * User model
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private String externalId; // ID from external storage system
    private String normalizedName; // Normalized name for URL
    private String name;
    private List<Book> books;
}
