package com.proyectodam.fichappclient.service.controlhorario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyectodam.fichappclient.enums.EstadoJornada;
import com.proyectodam.fichappclient.model.ContadorDTO;
import com.proyectodam.fichappclient.model.FichajeDTO;
import com.proyectodam.fichappclient.model.FichajeRequestDTO;
import com.proyectodam.fichappclient.model.HorasExtraDTO;
import com.proyectodam.fichappclient.util.ApiClient;

import java.io.IOException;
import java.util.List;

public class ControlHorarioEmpleadoService {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    public ControlHorarioEmpleadoService(ApiClient apiClient, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    public ControlHorarioEmpleadoService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void registrarFichaje(FichajeRequestDTO fichajeRequestDTO) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(fichajeRequestDTO);
        apiClient.post("/empleado/control-horario/fichar", requestBody);
    }

    public EstadoJornada obtenerEstado(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/estado/" + idEmpleado);
        return objectMapper.readValue(response, EstadoJornada.class);
    }

    public List<FichajeDTO> obtenerFichajesHoy(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/fichajes-hoy/" + idEmpleado);
        return objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(List.class, FichajeDTO.class));
    }

    public List<FichajeDTO> obtenerHistorico(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/fichajes-historico/" + idEmpleado);
        return objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(List.class, FichajeDTO.class));
    }

    public ContadorDTO obtenerContador(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/contador/" + idEmpleado);
        return objectMapper.readValue(response, ContadorDTO.class);
    }

    public HorasExtraDTO obtenerHorasExtra(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/horas-extra/" + idEmpleado);
        return objectMapper.readValue(response, HorasExtraDTO.class);
    }

    public double obtenerBolsaHoras(int idEmpleado) throws IOException, InterruptedException {
        String response = apiClient.get("/empleado/control-horario/bolsa-horas/" + idEmpleado);
        return objectMapper.readValue(response, Double.class);
    }
}
