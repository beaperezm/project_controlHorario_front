package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.DocumentoDTO;
import com.proyectodam.fichappclient.util.ApiClient;
import com.proyectodam.fichappclient.util.AppConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class DocumentoService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public DocumentoService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newHttpClient();
    }

    private String getBaseUrl() {
        return AppConfig.getInstance().getBaseUrl();
    }

    /**
     * Lista todos los documentos del sistema.
     */
    public List<DocumentoDTO> listarTodos() throws Exception {
        String response = apiClient.get("/documentos/all");
        return objectMapper.readValue(response, new TypeReference<List<DocumentoDTO>>() {
        });
    }

    /**
     * Sube un documento usando multipart/form-data.
     */
    public DocumentoDTO subirDocumento(File archivo, String nombreCustom, String categoria, Integer idEmpleado)
            throws Exception {
        String boundary = "BOUNDARY-" + UUID.randomUUID().toString();
        String url = getBaseUrl() + "/documentos/upload";

        HttpRequest.BodyPublisher bodyPublisher = ofMimeMultipartData(archivo, nombreCustom, categoria,
                String.valueOf(idEmpleado), boundary);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), DocumentoDTO.class);
        } else {
            throw new RuntimeException("Error al subir archivo: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Descarga el archivo binario desde el servidor.
     */
    public byte[] descargarDocumento(Long idDocumento) throws Exception {
        String url = getBaseUrl() + "/documentos/" + idDocumento + "/download";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new RuntimeException("Error al descargar archivo: " + response.statusCode());
        }
    }

    /**
     * Elimina el documento.
     */
    public void eliminarDocumento(Long idDocumento) throws Exception {
        apiClient.delete("/documentos/" + idDocumento);
    }

    // --- Helper para construir el Multipart Body de Java 11 ---
    private HttpRequest.BodyPublisher ofMimeMultipartData(File file, String nombreCustom, String categoria,
            String idEmpleado,
            String boundary) throws IOException {
        var byteArrays = new java.util.ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);

        // Parametro: nombreCustom
        if (nombreCustom != null && !nombreCustom.trim().isEmpty()) {
            byteArrays.add(separator);
            byteArrays
                    .add(("\"nombreCustom\"\r\n\r\n" + nombreCustom.trim() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }

        // Parametro: categoria
        byteArrays.add(separator);
        byteArrays.add(("\"categoria\"\r\n\r\n" + categoria + "\r\n").getBytes(StandardCharsets.UTF_8));

        // Parametro: idEmpleado
        byteArrays.add(separator);
        byteArrays.add(("\"idEmpleado\"\r\n\r\n" + idEmpleado + "\r\n").getBytes(StandardCharsets.UTF_8));

        // Parametro: archivo (fichero físico)
        byteArrays.add(separator);
        byteArrays.add(
                ("\"archivo\"; filename=\"" + file.getName() + "\"\r\nContent-Type: application/octet-stream\r\n\r\n")
                        .getBytes(StandardCharsets.UTF_8));

        try (InputStream is = new FileInputStream(file)) {
            byteArrays.add(is.readAllBytes());
        }
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));

        // Final boundary
        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
