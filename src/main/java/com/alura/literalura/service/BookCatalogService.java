package com.alura.literalura.service;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.HttpClientService;
import com.alura.literalura.model.Person;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookCatalogService {

    private final List<Book> catalog = new ArrayList<>(); // Lista para almacenar los libros
    private final HttpClientService clientService = new HttpClientService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Busca un libro por título en la API y guarda el primer resultado en el catálogo.
     *
     * @param title El título del libro a buscar.
     * @return El libro agregado al catálogo, o null si no se encuentra.
     */
    public Book searchAndAddBookByTitle(String title) {
        String responseJson = clientService.fetchBooks("search=" + title);
        if (responseJson == null) {
            System.out.println("No se pudo realizar la busqueda. Intenta de nuevo.");
            return null;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);
            JsonNode results = rootNode.path("results");

            if (results.isEmpty() || results.isMissingNode()) {
                System.out.println("No se encontraron libros con el título especificado.");
                return null;
            }

            // Filtrar los libros cuyo título contenga el texto ingresado
            for (JsonNode result : results) {
                String bookTitle = result.path("title").asText().toLowerCase();
                if (bookTitle.contains(title.toLowerCase())) {
                    // Crear un nuevo objeto Book usando los datos del resultado filtrado
                    Book book = new Book();
                    book.setId(result.path("id").asInt());
                    book.setTitle(result.path("title").asText());

                    // Mapear la lista de autores (Person)
                    List<Person> authors = new ArrayList<>();
                    result.path("authors").forEach(authorNode -> {
                        Person person = new Person();
                        person.setName(authorNode.path("name").asText());
                        person.setBirthYear(authorNode.path("birth_year").isMissingNode() ? null : authorNode.path("birth_year").asInt());
                        person.setDeathYear(authorNode.path("death_year").isMissingNode() ? null : authorNode.path("death_year").asInt());
                        authors.add(person);
                    });
                    book.setAuthors(authors);

                    // Obtener solo el primer idioma
                    book.setLanguages(List.of(result.path("languages").get(0).asText()));

                    // Mapear el número de descargas
                    book.setDownloadCount(result.path("download_count").asInt());

                    // Verificar si el libro ya está en el catálogo
                    if (catalog.stream().anyMatch(b -> b.getId() == book.getId())) {
                        System.out.println("El libro ya existe en el catalogo: " + book.getTitle());
                        return null;
                    }

                    // Agregar al catálogo
                    catalog.add(book);
                    System.out.println("Libro agregado al catalogo: " + book);
                    return book;
                }
            }

            System.out.println("No se encontraron libros con el título especificado.");
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Lista todos los libros en el catálogo.
     *
     * @return Lista de libros en el catálogo.
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(catalog);
    }

    /**
     * Lista los libros filtrados por idioma.
     *
     * @param language Idioma para filtrar.
     * @return Lista de libros en el idioma especificado.
     */
    public List<Book> getBooksByLanguage(String language) {
        return catalog.stream()
                .filter(book -> book.getLanguages().contains(language))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los autores de los libros en el catálogo.
     *
     * @return Lista de autores únicos.
     */
    public List<Person> getAllAuthors() {
        return catalog.stream()
                .map(book -> book.getAuthors().get(0)) // Tomar solo el primer autor
                .distinct() // Eliminar duplicados
                .collect(Collectors.toList());
    }

    /**
     * Lista autores que estaban vivos en un año específico.
     *
     * @param year Año a verificar.
     * @return Lista de autores vivos en el año dado.
     */
    public List<Person> getAuthorsAliveInYear(int year) {
        return getAllAuthors().stream()
                .filter(author -> {
                    Integer birthYear = author.getBirthYear();
                    Integer deathYear = author.getDeathYear();
                    // Autor estaba vivo si nació antes o en el año y no ha muerto o murió después del año.
                    return (birthYear != null && birthYear <= year) &&
                            (deathYear == null || deathYear >= year);
                })
                .collect(Collectors.toList());
    }
}

