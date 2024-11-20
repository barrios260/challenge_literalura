package com.alura.literalura.service;

import com.alura.literalura.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class     JsonParserService {

    private final ObjectMapper objectMapper;

    // Constructor para inicializar el ObjectMapper
    public JsonParserService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Convierte un JSON en un objeto Book.
     *
     * @param json El JSON de la API.
     * @return Un objeto Book.
     */
    public Book parseBook(String json) {
        try {
            return objectMapper.readValue(json, Book.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

