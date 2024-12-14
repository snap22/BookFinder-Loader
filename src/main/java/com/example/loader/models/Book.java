package com.example.loader.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Book model
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Book {
    private String name;
    private String author;
    private String isbn;
}
