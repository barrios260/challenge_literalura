package com.alura.literalura.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientService {

    private static final String BASE_URL = "https://gutendex.com/books";
    private final HttpClient client;

    // Constructor para inicializar el cliente HTTP con redirección automática
    public HttpClientService() {
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS) // Habilitar redirecciones automáticas
                .build();
    }

    /**
     * Método genérico para enviar solicitudes HTTP
     *
     * @param url      La URL completa para la solicitud.
     * @param method   El método HTTP (GET, POST, etc.).
     * @param headers  Encabezados adicionales en formato clave-valor.
     * @param body     El cuerpo de la solicitud (para POST o PUT).
     * @return HttpResponse<String> La respuesta completa.
     */
    public HttpResponse<String> sendRequest(String url, String method, String[][] headers, String body) {
        try {
            // Construcción de la solicitud HTTP
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url));

            // Configuración del método HTTP
            switch (method.toUpperCase()) {
                case "POST":
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
                    break;
                case "PUT":
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                default:
                    requestBuilder.GET();
                    break;
            }

            // Agregar encabezados si están definidos
            if (headers != null) {
                for (String[] header : headers) {
                    requestBuilder.header(header[0], header[1]);
                }
            }

            HttpRequest request = requestBuilder.build();

            // Enviar la solicitud y devolver la respuesta
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método para obtener una lista de libros con parámetros de consulta
     *
     * @param queryParams Los parámetros de consulta para la URL.
     * @return String El cuerpo de la respuesta.
     */
    public String fetchBooks(String queryParams) {
        String url = BASE_URL + (queryParams != null && !queryParams.isEmpty() ? "?" + queryParams : "");
        HttpResponse<String> response = sendRequest(url, "GET", null, null);

        if (response != null && response.statusCode() == 200) {
            return response.body();
        } else if (response != null) {
            handleErrorResponse(response);
        }
        return null;
    }

    /**
     * Método para obtener un libro individual por su ID
     *
     * @param id El ID del libro.
     * @return String El cuerpo de la respuesta.
     */
    public String fetchBookById(int id) {
        String url = BASE_URL + "/" + id;
        HttpResponse<String> response = sendRequest(url, "GET", null, null);

        if (response != null && response.statusCode() == 200) {
            return response.body();
        } else if (response != null) {
            handleErrorResponse(response);
        }
        return null;
    }

    /**
     * Método para manejar respuestas con errores
     *
     * @param response La respuesta HTTP con error.
     */
    private void handleErrorResponse(HttpResponse<String> response) {
        System.err.println("Error en la solicitud: Código de estado HTTP " + response.statusCode());
        if (response.headers().firstValue("Location").isPresent()) {
            System.err.println("Redirigido a: " + response.headers().firstValue("Location").get());
        }
        System.err.println("Cuerpo de la Respuesta de Error: " + response.body());
    }

    /**
     * Método para imprimir detalles de la respuesta HTTP
     *
     * @param response La respuesta HTTP a analizar.
     */
    public void printResponseDetails(HttpResponse<String> response) {
        if (response != null) {
            System.out.println("Código de Estado: " + response.statusCode());
            System.out.println("Encabezados: " + response.headers().map());
            System.out.println("Cuerpo de la Respuesta: " + response.body());
        } else {
            System.err.println("La respuesta es nula. Verifica la solicitud.");
        }
    }
}
