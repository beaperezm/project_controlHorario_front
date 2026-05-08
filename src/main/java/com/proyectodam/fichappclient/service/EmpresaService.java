package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichappclient.model.EmpresaDTO;
import com.proyectodam.fichappclient.util.AppConfig;
import com.proyectodam.fichappclient.util.SessionManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class EmpresaService {

    public EmpresaDTO obtenerConfiguracion() throws Exception {
        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/empresa/config");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        if (connection.getResponseCode() == 200) {
            try (InputStream is = connection.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(is, EmpresaDTO.class);
            }
        } else {
            throw new Exception("Error obteniendo configuración: " + connection.getResponseCode());
        }
    }

    public EmpresaDTO actualizarConfiguracion(EmpresaDTO empresaDTO) throws Exception {
        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/empresa/config");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonInputString = mapper.writeValueAsString(empresaDTO);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == 200) {
            try (InputStream is = connection.getInputStream()) {
                return mapper.readValue(is, EmpresaDTO.class);
            }
        } else {
            throw new Exception("Error actualizando configuración: " + connection.getResponseCode());
        }
    }

    public void subirLogo(File file) throws Exception {
        String boundary = "----FichAppBoundary" + System.currentTimeMillis();
        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/empresa/logo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) contentType = "application/octet-stream";

        try (OutputStream os = connection.getOutputStream()) {
            String header = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n"
                    + "Content-Type: " + contentType + "\r\n\r\n";
            os.write(header.getBytes("utf-8"));
            Files.copy(file.toPath(), os);
            os.write(("\r\n--" + boundary + "--\r\n").getBytes("utf-8"));
        }

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new Exception("Error al subir el logo: " + code);
        }
    }
}
