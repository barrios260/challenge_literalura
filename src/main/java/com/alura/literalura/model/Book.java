package com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar propiedades desconocidas
public class Book {

    private int id;

    private String title;

    @JsonAlias("authors") // Alias para mapear correctamente
    private List<Person> authors;

    private List<String> subjects;

    private List<String> bookshelves;

    private List<String> languages;

    private boolean copyright;

    @JsonAlias("download_count") // Alias para mapear el atributo del JSON
    private int downloadCount;

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

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    // Método toString para mostrar los datos del libro
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", subjects=" + subjects +
                ", bookshelves=" + bookshelves +
                ", languages=" + languages +
                ", copyright=" + copyright +
                ", downloadCount=" + downloadCount +
                '}';
    }
}

