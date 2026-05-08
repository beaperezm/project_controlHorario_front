package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichappclient.model.LoginRequestDTO;
import com.proyectodam.fichappclient.model.LoginResponseDTO;
import com.proyectodam.fichappclient.util.AppConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {
    private String accessToken;
    private String refreshToken;
    private int idEmpleado;
    private String role;

    public LoginResponseDTO login(String email, String password) throws IOException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);

        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/auth/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes("utf-8"));
        }

        int status = connection.getResponseCode();
        InputStream inputStream = (status < 400) ? connection.getInputStream() : connection.getErrorStream();

        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                respuesta.append(linea.trim());
            }
        }

        if (status == 200) {
            LoginResponseDTO loginResponseDTO = objectMapper.readValue(respuesta.toString(), LoginResponseDTO.class);
            this.accessToken = loginResponseDTO.getAccessToken();
            this.refreshToken = loginResponseDTO.getRefreshToken();
            this.idEmpleado = loginResponseDTO.getIdEmpleado();
            this.role = loginResponseDTO.getRole();
            return loginResponseDTO;
        } else if (status == 403) {
            // Cuenta no activada en Supabase
            throw new AccountNotActivatedException("La cuenta existe pero no está activada en Supabase.");
        } else {
            throw new RuntimeException("Login fallido: " + respuesta);
        }
    }

    public LoginResponseDTO activarCuenta(String email, String password) throws IOException {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginRequestDTO);

        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/auth/activar-cuenta");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.getBytes());
        }

        int status = connection.getResponseCode();
        if (status == 200) {
            return mapper.readValue(connection.getInputStream(), LoginResponseDTO.class);
        } else {
            // Leer el cuerpo del error para obtener el mensaje real del backend
            String errorBody = "";
            try (java.io.InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    errorBody = new String(errorStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                }
            }
            // Intentar extraer el "message" del JSON de error
            String mensaje = "Error activando cuenta (HTTP " + status + ")";
            if (errorBody.contains("\"message\"")) {
                try {
                    int start = errorBody.indexOf("\"message\":\"") + 11;
                    int end = errorBody.indexOf("\"", start);
                    if (start > 10 && end > start) {
                        mensaje = errorBody.substring(start, end);
                    }
                } catch (Exception ignored) {}
            }
            throw new RuntimeException(mensaje);
        }
    }

    public void cambiarPassword(int idEmpleado, String nuevaPassword) throws IOException {
        String currentToken = this.accessToken != null ? this.accessToken : com.proyectodam.fichappclient.util.SessionManager.getInstance().getAccessToken();
        String json = "{\"newPassword\":\"" + nuevaPassword + "\"}";
        
        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/empleados/" + idEmpleado + "/cambiar-password");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + currentToken);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(json.getBytes());
        }

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Error al cambiar la contraseña");
        }
    }

    public com.proyectodam.fichappclient.model.EmpleadoDetalleDTO getEmpleadoDetalle(int idEmpleado) throws IOException {
        String currentToken = this.accessToken != null ? this.accessToken : com.proyectodam.fichappclient.util.SessionManager.getInstance().getAccessToken();
        URL url = new URL(AppConfig.getInstance().getBaseUrl() + "/empleados/detalle/" + idEmpleado);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + currentToken);
        connection.setRequestProperty("Content-Type", "application/json");

        int status = connection.getResponseCode();
        if (status == 200) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(connection.getInputStream(), com.proyectodam.fichappclient.model.EmpleadoDetalleDTO.class);
        } else {
            throw new RuntimeException("Error al obtener detalle del empleado: HTTP " + status);
        }
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public int getIdEmpleado() { return idEmpleado; }
    public String getRole() { return role; }
}
