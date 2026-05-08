package com.proyectodam.fichappclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.model.VacacionesDTO;
import com.proyectodam.fichappclient.util.ApiClient;

import java.util.List;

public class VacacionesService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    public VacacionesService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<VacacionesDTO> obtenerTodas() throws Exception {
        String response = apiClient.get("/vacaciones/todas");
        return objectMapper.readValue(response, new TypeReference<List<VacacionesDTO>>() {});
    }

    public List<VacacionesDTO> obtenerPorEmpleado(Integer idEmpleado) throws Exception {
        String response = apiClient.get("/vacaciones/empleado/" + idEmpleado);
        return objectMapper.readValue(response, new TypeReference<List<VacacionesDTO>>() {});
    }

    public VacacionesDTO aprobarSolicitud(Integer idSolicitud) throws Exception {
        String response = apiClient.put("/vacaciones/" + idSolicitud + "/aprobar", "");
        return objectMapper.readValue(response, VacacionesDTO.class);
    }

    public VacacionesDTO rechazarSolicitud(Integer idSolicitud, String comentario) throws Exception {
        // Asumiendo que el comentario va como parámetro de query
        String endpoint = "/vacaciones/" + idSolicitud + "/rechazar?comentario=" + java.net.URLEncoder.encode(comentario, "UTF-8");
        String response = apiClient.put(endpoint, "");
        return objectMapper.readValue(response, VacacionesDTO.class);
    }

    public VacacionesDTO solicitarVacaciones(Integer idEmpleado, VacacionesDTO peticion) throws Exception {
        String json = objectMapper.writeValueAsString(peticion);
        String endpoint = "/vacaciones/solicitar?idEmpleado=" + idEmpleado;
        String response = apiClient.post(endpoint, json);
        return objectMapper.readValue(response, VacacionesDTO.class);
    }
}
