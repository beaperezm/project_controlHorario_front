package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.DepartamentoDTO;
import com.proyectodam.fichappclient.util.ApiClient;

import java.util.List;

public class DepartamentoService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "/departamentos";

    public DepartamentoService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<DepartamentoDTO> obtenerTodos() throws Exception {
        String response = apiClient.get(API_URL + "/all");
        return objectMapper.readValue(response, new TypeReference<List<DepartamentoDTO>>() {});
    }

    public DepartamentoDTO guardar(DepartamentoDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        String response = apiClient.post(API_URL, json);
        return objectMapper.readValue(response, DepartamentoDTO.class);
    }

    public void eliminar(int id) throws Exception {
        apiClient.delete(API_URL + "/" + id);
    }
}
