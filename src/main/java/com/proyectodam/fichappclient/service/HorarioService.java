package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.HorarioDTO;
import com.proyectodam.fichappclient.util.ApiClient;

import java.util.List;

public class HorarioService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "/horarios";

    public HorarioService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<HorarioDTO> obtenerTodos() throws Exception {
        String response = apiClient.get(API_URL + "/all");
        return objectMapper.readValue(response, new TypeReference<List<HorarioDTO>>() {});
    }

    public HorarioDTO guardar(HorarioDTO dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        String response = apiClient.post(API_URL, json);
        return objectMapper.readValue(response, HorarioDTO.class);
    }

    public void eliminar(int id) throws Exception {
        apiClient.delete(API_URL + "/" + id);
    }
}
