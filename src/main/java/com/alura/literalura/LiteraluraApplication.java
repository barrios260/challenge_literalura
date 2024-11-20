package com.alura.literalura;

import com.alura.literalura.model.ApiResponse;
import com.alura.literalura.model.Book;
import com.alura.literalura.model.HttpClientService;
import com.alura.literalura.model.Person;
import com.alura.literalura.service.BookCatalogService;
import com.alura.literalura.service.JsonParserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Llamar al metodo para mostrar el menú
		showMenu();
	}

	private void showMenu() {
		// Inicializar servicios
		HttpClientService clientService = new HttpClientService();
		JsonParserService parserService = new JsonParserService();
		BookCatalogService bookCatalogService = new BookCatalogService(); // Servicio para el catálogo de libros

		// Scanner para capturar la entrada del usuario
		Scanner scanner = new Scanner(System.in);

		// Bucle del menú
		boolean running = true;
		while (running) {
			System.out.println("\n===== Menu Literalura =====");
			System.out.println("1. Buscar un libro por ID");
			System.out.println("2. Buscar libros por idioma y popularidad");
			System.out.println("3. Buscar libros por titulo y agregarlos al catalogo");
			System.out.println("4. Listar todos los libros en el catalogo");
			System.out.println("5. Listar libros por idioma en el catálogo");
			System.out.println("6. Listar todos los autores");
			System.out.println("7. Listar autores vivos en un año específico");
			System.out.println("8. Salir");
			System.out.print("Selecciona una opción: ");

			String option = scanner.nextLine();

			switch (option) {
				case "1": // Buscar un libro por ID
					System.out.print("Ingresa el ID del libro: ");
					String idInput = scanner.nextLine();
					try {
						int bookId = Integer.parseInt(idInput);
						String bookJson = clientService.fetchBookById(bookId);
						if (bookJson != null) {
							Book book = parserService.parseBook(bookJson);
							System.out.println("\nDetalles del libro:");
							System.out.println(book != null ? book : "No se encontro el libro.");
						} else {
							System.out.println("No se pudo obtener el libro. Revisa el ID.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Error: El ID debe ser un número entero.");
					}
					break;

				case "2": // Buscar libros por idioma y popularidad
					System.out.print("Ingresa el idioma (código ISO, por ejemplo 'en'): ");
					String language = scanner.nextLine().toLowerCase();
					String booksJson = clientService.fetchBooks("languages=" + language + "&sort=popular");
					if (booksJson != null) {
						ApiResponse apiResponse = parserService.parseApiResponse(booksJson);
						if (apiResponse != null && apiResponse.getResults() != null && !apiResponse.getResults().isEmpty()) {
							System.out.println("\nLista de libros por idioma:");
							apiResponse.getResults().stream()
									.filter(book -> book.getLanguages().contains(language))
									.forEach(System.out::println);
						} else {
							System.out.println("No se encontraron libros para el idioma especificado.");
						}
					} else {
						System.out.println("No se pudo obtener la lista de libros.");
					}
					break;

				case "3": // Buscar libros por título y agregarlos al catálogo
					System.out.print("Ingresa el titulo del libro: ");
					String title = scanner.nextLine();
					Book addedBook = bookCatalogService.searchAndAddBookByTitle(title);
					if (addedBook != null) {
						System.out.println("Libro agregado al catálogo: " + addedBook);
					}
					break;

				case "4": // Listar todos los libros en el catálogo
					System.out.println("\nLista de todos los libros en el catalogo:");
					List<Book> allBooks = bookCatalogService.getAllBooks();
					if (allBooks.isEmpty()) {
						System.out.println("El catalogo esta vacío.");
					} else {
						allBooks.forEach(System.out::println);
					}
					break;

				case "5": // Listar libros por idioma en el catálogo
					System.out.print("Ingresa el idioma (código ISO, por ejemplo 'en'): ");
					String catalogLanguage = scanner.nextLine();
					System.out.println("\nLista de libros en el idioma '" + catalogLanguage + "':");
					List<Book> booksByLanguage = bookCatalogService.getBooksByLanguage(catalogLanguage);
					if (booksByLanguage.isEmpty()) {
						System.out.println("No hay libros en el catalogo con el idioma especificado.");
					} else {
						booksByLanguage.forEach(System.out::println);
					}
					break;

				case "6": // Listar todos los autores
					System.out.println("\nLista de autores:");
					List<Person> authors = bookCatalogService.getAllAuthors();
					if (authors.isEmpty()) {
						System.out.println("No hay autores en el catálogo.");
					} else {
						authors.forEach(System.out::println);
					}
					break;

				case "7": // Listar autores vivos en un año específico
					System.out.print("Ingresa el año para buscar autores vivos: ");
					try {
						int year = Integer.parseInt(scanner.nextLine());
						List<Person> authorsAlive = bookCatalogService.getAuthorsAliveInYear(year);
						if (authorsAlive.isEmpty()) {
							System.out.println("No se encontraron autores vivos en el año " + year + ".");
						} else {
							System.out.println("\nAutores vivos en el año " + year + ":");
							authorsAlive.forEach(System.out::println);
						}
					} catch (NumberFormatException e) {
						System.out.println("Por favor ingresa un número válido para el año.");
					}
					break;

				case "8": // Salir
					System.out.println("¡Gracias por usar la aplicación!");
					running = false;
					break;
			}
		}

		scanner.close();
	}

}
