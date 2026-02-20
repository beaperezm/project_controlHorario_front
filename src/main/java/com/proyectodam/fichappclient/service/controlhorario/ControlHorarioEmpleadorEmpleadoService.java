package com.proyectodam.fichappclient.service.controlhorario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.util.ApiClient;

import java.util.List;

public class ControlHorarioEmpleadorEmpleadoService {

    private ApiClient apiClient;
    private ObjectMapper objectMapper;

    public ControlHorarioEmpleadorEmpleadoService() {
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void borradoLogicoEmpleado(int idEmpleado) throws Exception {
        apiClient.post("/empleados/empleado/" + idEmpleado + "/baja", "");
    }

    public List<EmpleadoDTO> getAllEmpleados() throws Exception{
        String response = apiClient.get("/empleados/all/activos");
        return objectMapper.readValue(response, new TypeReference<List<EmpleadoDTO>>() {});
    }

}
