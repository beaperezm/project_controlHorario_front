package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectodam.fichappclient.model.LoginRequestDTO;
import com.proyectodam.fichappclient.model.LoginResponseDTO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    private static final String URL = "http://localhost:8080/auth/login";
    private String accessToken;
    private String refreshToken;
    private int idEmpleado;
    private String role;

    public LoginResponseDTO login(String email, String password) throws IOException {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(loginRequestDTO);

        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(request.getBytes("utf-8"));
        }

        int status = connection.getResponseCode();

        InputStream inputStream = (status < 400) ? connection.getInputStream() : connection.getErrorStream();
        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
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
        } else throw new RuntimeException("Login fallido " + respuesta.toString());

    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getRole() {
        return role;
    }

    public LoginResponseDTO activarCuenta(String email, String password) throws IOException {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(loginRequestDTO);

        URL url = new URL("http://localhost:8080/auth/activar-cuenta");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setDoOutput(true);

        try(OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(json.getBytes());
        }

        if(httpURLConnection.getResponseCode() == 200) {
            return mapper.readValue(httpURLConnection.getInputStream(), LoginResponseDTO.class);
        } else {
                throw new RuntimeException("Error activando cuenta, del backend");
            }

        }
}
