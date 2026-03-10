package com.proyectodam.fichappclient.util;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP personalizado para realizar peticiones a la API REST.
 * Encapsula la lógica de conexión, cabeceras y manejo básico de respuestas.
 */
public class ApiClient {

    // private final String BASE_URL = "http://localhost:8082/api";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String getBaseUrl() {
        return AppConfig.getInstance().getBaseUrl();
    }

    public String get(String endpoint) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return sendRequest(request);
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String delete(String endpoint) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return sendRequest(request);
    }

    private String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            String errorMsg = response.body();
            // Intentar extraer el "message" del JSON de Spring Boot
            if (errorMsg != null && errorMsg.contains("\"message\"")) {
                try {
                    int start = errorMsg.indexOf("\"message\":\"") + 11;
                    int end = errorMsg.indexOf("\"", start);
                    if (start > 10 && end > start) {
                        String extractedMsg = errorMsg.substring(start, end);
                        if (!extractedMsg.isEmpty() && !extractedMsg.equals("No message available")) {
                            errorMsg = extractedMsg;
                        }
                    }
                } catch (Exception e) {
                    // Si falla el parseo simple, usamos el body entero
                }
            }
            if (response.statusCode() == 500 && errorMsg.contains("ConstraintViolationException")
                    || errorMsg.contains("DataIntegrityViolationException")) {
                errorMsg = "Ha ocurrido un error de integridad en la base de datos (por ejemplo, restricción de estado).";
            }
            throw new RuntimeException("Error: " + response.statusCode() + " - " + errorMsg);
        }
    }

}
