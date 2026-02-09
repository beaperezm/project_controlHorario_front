package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.AltaRapidaEmpleadoDTO;
import com.proyectodam.fichappclient.model.DepartamentoDTO;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.model.RolDTO;
import com.proyectodam.fichappclient.util.ApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ControlHorarioService {

    private ApiClient apiClient = new ApiClient();
    private ObjectMapper objectMapper = new ObjectMapper();


    public ControlHorarioService(ApiClient apiClient, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    public ControlHorarioService() {
        this.apiClient = apiClient = new ApiClient();
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

        public EmpleadoDTO altaRapidaEmpleado (AltaRapidaEmpleadoDTO altaRapidaEmpleadoDTO) throws Exception{
            String requestBody = objectMapper.writeValueAsString(altaRapidaEmpleadoDTO);
            String response = apiClient.post("/empleados/alta-rapida", requestBody);
            return objectMapper.readValue(response, EmpleadoDTO.class);
        }


}
