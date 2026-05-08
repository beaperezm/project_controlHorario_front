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

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ApiClient() {
    }

    private String getBaseUrl() {
        return AppConfig.getInstance().getBaseUrl();
    }

    private HttpRequest.Builder baseBuilder(String url) {
        String token = SessionManager.getInstance().getAccessToken();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");

        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    public String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = baseBuilder(getBaseUrl() + endpoint).GET().build();
        return sendRequest(request);
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = baseBuilder(getBaseUrl() + endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = baseBuilder(getBaseUrl() + endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    public String delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = baseBuilder(getBaseUrl() + endpoint).DELETE().build();
        return sendRequest(request);
    }

    public byte[] download(String endpoint) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;

        HttpRequest request = baseBuilder(fullUrl).GET().build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Error al descargar archivo: " + response.statusCode());
        }
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
