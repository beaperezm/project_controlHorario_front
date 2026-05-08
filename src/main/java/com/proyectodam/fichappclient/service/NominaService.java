package com.proyectodam.fichappclient.service;

import com.proyectodam.fichappclient.util.ApiClient;

public class NominaService {

    private final ApiClient apiClient;

    public NominaService() {
        this.apiClient = new ApiClient();
    }

    public void signNomina(Long nominaId, Integer idEmpleado, String password) throws Exception {
        String endpoint = "/nominas/" + nominaId + "/sign?idEmpleado=" + idEmpleado;

        // Escapar comillas dobles y barras invertidas en la contraseña para formar un JSON válido
        
        String safePassword = password.replace("\\", "\\\\").replace("\"", "\\\"");
        String jsonBody = "{\"password\": \"" + safePassword + "\"}";

        apiClient.post(endpoint, jsonBody);

        // ApiClient.post lanza una excepción en respuestas que no sean 200, por lo que no es necesario
        // verificar el código de estado
    }

    public void sendEmail(Long nominaId) throws Exception {
        String endpoint = "/nominas/" + nominaId + "/send-email";
        apiClient.post(endpoint, "");
    }
}
