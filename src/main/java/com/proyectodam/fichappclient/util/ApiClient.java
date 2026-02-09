package com.proyectodam.fichappclient.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private final String BASE_URL = "http://localhost:8082";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String get(String endpoint) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return sendRequest(request);
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return sendRequest(request);
    }

    public String delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return sendRequest(request);
    }


    private String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() >= 200 & response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Error en la API: Código " + response.statusCode() + " - " + response.body());
        }
    }

}
