package com.alura.literalura.service;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.PersonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCatalogService {

    private final List<BookDto> catalog = new ArrayList<>(); // Lista para almacenar los libros
    private final HttpClientService clientService = new HttpClientService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;


    public BookCatalogService(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

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
                    BookDto book = new BookDto();
                    book.setId(result.path("id").asInt());
                    book.setTitle(result.path("title").asText());

                    // Mapear la lista de autores (Person)
                    List<PersonDto>authors=new ArrayList<>();
                    JsonNode firstAuthor = result.path("authors").get(0); // Solo el primer autor
                    if (firstAuthor != null) {
                        PersonDto person = new PersonDto();
                        person.setName(firstAuthor.path("name").asText());
                        person.setBirthYear(firstAuthor.path("birth_year").isMissingNode() ? null : firstAuthor.path("birth_year").asInt());
                        person.setDeathYear(firstAuthor.path("death_year").isMissingNode() ? null : firstAuthor.path("death_year").asInt());
                        authors.add(person);
                    }
                    book.setAuthor(authors);


                    // Obtener solo el primer idioma
                    book.setLanguage(result.path("languages").get(0).asText());

                    // Mapear el número de descargas
                    book.setDownloadCount(result.path("download_count").asInt());

                    // Verificar si el libro ya está en el catálogo
                    if (catalog.stream().anyMatch(b -> b.getId() == book.getId())) {
                        System.out.println("El libro ya existe en el catalogo: " + book.getTitle());
                        return null;
                    }
                    // Agregar al catálogo
                    //catalog.add(book);
                    Book book1 = new Book();
                    book1.setTitle(book.getTitle());
                    book1.setLanguage(book.getLanguage());
                    book1.setDownloadCount(book.getDownloadCount());
                    book1.setId(book.getId());
                    Person p1 = new Person();
                    p1.setName(book.getAuthor().get(0).getName());
                    p1.setBirthYear(book.getAuthor().get(0).getBirthYear());
                    p1.setDeathYear(book.getAuthor().get(0).getDeathYear());
                    Person p2=personRepository.save(p1);
                    book1.setAuthor(p2);
                    Book book2= bookRepository.save(book1);
                    return book2;
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
    public List<BookDto> getAllBooks() {
        return new ArrayList<>(catalog);
    }

    /**
     * Lista los libros filtrados por idioma.
     *
     * @param language Idioma para filtrar.
     * @return Lista de libros en el idioma especificado.
     */
    public List<BookDto> getBooksByLanguage(String language) {
        return catalog.stream()
                .filter(book -> book.getLanguage().contains(language))
                .collect(Collectors.toList());
    }

    /**
     * Lista todos los autores de los libros en el catálogo.
     *
     * @return Lista de autores únicos.
     */
    public List<PersonDto> getAllAuthors() {
        return catalog.stream().map(bookDto -> bookDto.getAuthor().get(0)).toList();
    }

    /**
     * Lista autores que estaban vivos en un año específico.
     *
     * @param year Año a verificar.
     * @return Lista de autores vivos en el año dado.
     */
    public List<PersonDto> getAuthorsAliveInYear(int year) {
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

