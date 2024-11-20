package com.alura.literalura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.HttpClientService;
import com.alura.literalura.service.JsonParserService;

@SpringBootApplication
public class LiteraluraApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);

		HttpClientService clientService = new HttpClientService();
		JsonParserService parserService = new JsonParserService();

		// Obtener detalles de un libro por ID
		int bookId = 1342; // ID de ejemplo
		String bookJson = clientService.fetchBookById(bookId);

		if (bookJson != null) {
			Book book = parserService.parseBook(bookJson);
			if (book != null) {
				System.out.println("Titulo: " + book.getTitle());
				System.out.println("Autores: ");
				book.getAuthors().forEach(author -> System.out.println("- " + author.getName()));
				System.out.println("Descargas: " + book.getDownload_count());
			}
		}
	}
}