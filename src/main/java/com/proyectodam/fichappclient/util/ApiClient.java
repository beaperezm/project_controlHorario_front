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
        // System.out.println("Solicitando: " + fullUrl); // Log para desarrollo

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return sendRequest(request);
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;
        System.out.println("Requesting (POST): " + fullUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;
        System.out.println("Requesting (PUT): " + fullUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String delete(String endpoint) throws IOException, InterruptedException {
        String fullUrl = getBaseUrl() + endpoint;
        System.out.println("Requesting (DELETE): " + fullUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return sendRequest(request);
    }

    private String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body ( 200 characters): " +
                (response.body().length() > 200 ? response.body().substring(0, 200) : response.body()));

        if (response.statusCode() >= 200 & response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Error: Status code " + response.statusCode() + " - " + response.body());
        }
    }

}
