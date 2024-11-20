package com.alura.literalura.service;

import com.alura.literalura.model.ApiResponse;
import com.alura.literalura.model.Book;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonParserService {

    private final ObjectMapper objectMapper;

    public JsonParserService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    /**
     * Convierte un JSON en una lista de objetos Book.
     *
     * @param json El JSON de la API.
     * @return Una lista de objetos Book.
     */
    public List<Book> parseBooks(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Book>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convierte un JSON en un objeto ApiResponse.
     *
     * @param json El JSON de la API.
     * @return Un objeto ApiResponse que contiene los resultados y metadatos.
     */
    public ApiResponse parseApiResponse(String json) {
        try {
            return objectMapper.readValue(json, ApiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}



