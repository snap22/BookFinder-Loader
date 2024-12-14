package com.example.loader.mappers;

import com.example.loader.dto.BookJson;
import com.example.loader.models.Book;

/**
 * Mapper for Book model
 */
public class BookMapper {
    public static BookJson toBookJson(Book book) {
        return new BookJson(book.getName(), book.getAuthor());
    }
}
