package com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar propiedades desconocidas
public class Book {
    private int id;
    private String title;
    private List<Person> authors;
    private List<String> subjects;
    private List<String> bookshelves;
    private List<String> languages;
    private boolean copyright;
    private int download_count;

    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<Person> getAuthors() {
        return authors;
    }
    public void setAuthors(List<Person> authors) {
        this.authors = authors;
    }
    public List<String> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
    public List<String> getBookshelves() {
        return bookshelves;
    }
    public void setBookshelves(List<String> bookshelves) {
        this.bookshelves = bookshelves;
    }
    public List<String> getLanguages() {
        return languages;
    }
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    public boolean isCopyright() {
        return copyright;
    }
    public void setCopyright(boolean copyright) {
        this.copyright = copyright;
    }
    public int getDownload_count() {
        return download_count;
    }
    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }
}

