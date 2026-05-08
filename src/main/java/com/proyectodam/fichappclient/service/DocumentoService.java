package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.DocumentoDTO;
import com.proyectodam.fichappclient.util.ApiClient;
import com.proyectodam.fichappclient.util.AppConfig;
import com.proyectodam.fichappclient.util.SessionManager;

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
     * Obtiene los años ("tiempos") únicos disponibles.
     */
    public List<String> obtenerTiemposDisponibles(String categoria) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("/documentos/tiempo");
        if (categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todos")) {
            urlBuilder.append("?categoria=").append(java.net.URLEncoder.encode(categoria, StandardCharsets.UTF_8));
        }
        String response = apiClient.get(urlBuilder.toString());
        return objectMapper.readValue(response, new TypeReference<List<String>>() {});
    }

    /**
     * Obtiene una página filtrada de documentos.
     */
    public com.proyectodam.fichappclient.model.PageResponseDTO<DocumentoDTO> obtenerPaginados(int page, int size, String categoria, Integer idEmpleado, String searchQuery, String year, String departamento) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("/documentos/paginas?page=").append(page).append("&size=").append(size);
        
        if (categoria != null && !categoria.isEmpty() && !categoria.equalsIgnoreCase("Todos")) {
            urlBuilder.append("&categoria=").append(java.net.URLEncoder.encode(categoria, StandardCharsets.UTF_8));
        }
        
        if (idEmpleado != null) urlBuilder.append("&idEmpleado=").append(idEmpleado);
        if (searchQuery != null && !searchQuery.isEmpty()) urlBuilder.append("&searchQuery=").append(java.net.URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));
        if (year != null && !year.isEmpty() && !year.equalsIgnoreCase("Todos")) urlBuilder.append("&year=").append(java.net.URLEncoder.encode(year, StandardCharsets.UTF_8));
        if (departamento != null && !departamento.isEmpty() && !departamento.equalsIgnoreCase("Todos")) urlBuilder.append("&departamento=").append(java.net.URLEncoder.encode(departamento, StandardCharsets.UTF_8));
        
        String response = apiClient.get(urlBuilder.toString());
        return objectMapper.readValue(response, new TypeReference<com.proyectodam.fichappclient.model.PageResponseDTO<DocumentoDTO>>() {});
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

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublisher);

        // Añadir token de autorización si existe
        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = requestBuilder.build();
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
        return apiClient.download("/documentos/" + idDocumento + "/download");
    }

    /**
     * Elimina el documento.
     */
    public void eliminarDocumento(Long idDocumento) throws Exception {
        apiClient.delete("/documentos/" + idDocumento);
    }

    // --- Método auxiliar para construir el cuerpo Multipart de Java 11 ---
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

        // Límite final
        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
