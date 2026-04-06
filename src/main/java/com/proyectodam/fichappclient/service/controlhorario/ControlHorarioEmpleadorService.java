package com.proyectodam.fichappclient.service.controlhorario;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.util.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class ControlHorarioEmpleadorService {

    private ApiClient apiClient = new ApiClient();
    private ObjectMapper objectMapper = new ObjectMapper();


    public ControlHorarioEmpleadorService(ApiClient apiClient, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    public ControlHorarioEmpleadorService() {
        this.apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    public List<DepartamentoDTO> getAllDepartamentos() throws Exception{
        String response = apiClient.get("/departamentos/all");
        return objectMapper.readValue(response, new TypeReference<List<DepartamentoDTO>>() {});
    }

    public List<RolDTO> getAllRoles() throws Exception{
        String response = apiClient.get("/roles/all");
        return objectMapper.readValue(response, new TypeReference<List<RolDTO>>() {});
    }

    public List<HorarioDTO> getAllHorarios() throws Exception{
        String response = apiClient.get("/horarios/all");
        return objectMapper.readValue(response, new TypeReference<List<HorarioDTO>>() {});
    }


    public EmpleadoDTO altaRapidaEmpleado (AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) throws Exception{
        String requestBody = objectMapper.writeValueAsString(altaRapidaEmpleadoDTO);
        String response = apiClient.post("/empleados/alta-rapida", requestBody);
        return objectMapper.readValue(response, EmpleadoDTO.class);
        }

        public void borrarEmpleado(int idEmpleado) throws Exception {
            apiClient.delete("/empleados/empleado/" + idEmpleado);
        }

        public EmpleadoDTO editarEmpleado(int idEmpleado, AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) throws IOException, InterruptedException {
            String requestBody = objectMapper.writeValueAsString(altaRapidaEmpleadoDTO);
            String respuesta = apiClient.put("/empleados/empleado/" + idEmpleado, requestBody);
            return objectMapper.readValue(respuesta, EmpleadoDTO.class);
        }

    public List<EmpleadoDTO> getAllEmpleados() throws Exception{
        String response = apiClient.get("/empleados/all");
        return objectMapper.readValue(response, new TypeReference<List<EmpleadoDTO>>() {});
    }


}
