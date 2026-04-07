package com.proyectodam.fichappclient.service;

import com.proyectodam.fichappclient.util.ApiClient;

public class NominaService {

    private final ApiClient apiClient;

    public NominaService() {
        this.apiClient = new ApiClient();
    }

    public void signNomina(Long nominaId, Integer idEmpleado, String password) throws Exception {
        String endpoint = "/nominas/" + nominaId + "/sign?idEmpleado=" + idEmpleado;

        // Escape double quotes and backslashes in password to form valid JSON
        String safePassword = password.replace("\\", "\\\\").replace("\"", "\\\"");
        String jsonBody = "{\"password\": \"" + safePassword + "\"}";

        apiClient.postNominaYControladorCalendario(endpoint, jsonBody);
        // ApiClient.post throws exception on non-200 responses, so no need to check
        // status code
    }

    public void sendEmail(Long nominaId) throws Exception {
        String endpoint = "/nominas/" + nominaId + "/send-email";
        apiClient.postNominaYControladorCalendario(endpoint, "");
    }
}
