package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.enums.EstadoJornada;
import com.proyectodam.fichappclient.enums.MetodoFichaje;
import com.proyectodam.fichappclient.enums.TipoEventoFichaje;
import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadoService;
import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.util.AlertUtils;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ControlHorarioEmpleadoController {

    @FXML private VBox rootContainer;
    @FXML private Label nombreLabel, contadorLabel, horasExtraLabel, bolsaHorasLabel;
    @FXML private ImageView imgPerfil;
    @FXML private Button btnEntrada, btnDescanso, btnSalida;
    @FXML private TableView<FichajeHoyDTO> tablaHoy;
    @FXML private TableView<FichajeDTO> tablaHistorico;
    @FXML private TableColumn<FichajeHoyDTO, String> colFechaHoy, colEntradaHoy, colDescansoHoy, colSalidaHoy, colTipoHoy, colServidorHoy;
    @FXML private TableColumn<FichajeDTO, String> colFechaHist, colTipoHist, colMetodoHist, colHoraHist;

    private final ControlHorarioEmpleadoService controlHorarioEmpleadoService = new ControlHorarioEmpleadoService();
    private Timeline timelineContador, timelineActualizacion, timelineHorasExtra;
    private boolean enPausa = false;

    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        try {
            // Aplicar estilos tipográficos directamente (máxima prioridad, no depende de CSS cascada)
            contadorLabel.setStyle(
                "-fx-font-size: 52px; " +
                "-fx-font-family: 'Consolas', 'Courier New', 'Monospaced'; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff;"
            );
            horasExtraLabel.setStyle(
                "-fx-font-size: 22px; " +
                "-fx-font-family: 'Consolas', 'Courier New', 'Monospaced'; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff;"
            );
            bolsaHorasLabel.setStyle(
                "-fx-font-size: 22px; " +
                "-fx-font-family: 'Consolas', 'Courier New', 'Monospaced'; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff;"
            );

            configurarTablas();
            tablaHoy.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablaHistorico.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            cargarEstado();
            cargarTablas();
            iniciarActualizacionAutomatica();
            iniciarContador();
            iniciarActualizacionHorasExtra();

            EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();
            if (empleadoDTO != null) {
                nombreLabel.setText(empleadoDTO.getNombre() + " " + empleadoDTO.getApellidos());
                
                if (empleadoDTO.getImgPerfil() != null && !empleadoDTO.getImgPerfil().isBlank()) {
                    try {
                        imgPerfil.setImage(new Image(empleadoDTO.getImgPerfil()));
                    } catch (Exception e) {
                        System.err.println("Error al cargar la imagen de perfil: " + empleadoDTO.getImgPerfil());
                    }
                }
            } else {
                System.err.println("ADVERTENCIA: No se encontró empleado logueado en SessionData");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR durante la inicialización de ControlHorarioEmpleadoController:");
            e.printStackTrace();
        }
    }

    /*private void estadoDentro() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(false);
        btnSalida.setDisable(false);
    }

    private void estadoEnPausa() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(false);
        btnSalida.setDisable(true);
    }

    private void desactivarBotones() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(true);
        btnSalida.setDisable(true);
    }*/

    @FXML
    private void ficharEntrada() {
        registrar(TipoEventoFichaje.ENTRADA);
    }

    @FXML
    private void ficharSalida() {
        registrar(TipoEventoFichaje.SALIDA);
    }

    @FXML
    private void ficharDescanso() {
        if (!enPausa) {
            registrar(TipoEventoFichaje.PAUSA_INICIO);
        } else {
            registrar(TipoEventoFichaje.PAUSA_FIN);
        }
    }

    private void registrar(TipoEventoFichaje tipoEventoFichaje) {
        EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();
        if (empleadoDTO == null) {
            AlertUtils.mostrarError("Error", "No hay empleado logueado");
            return;
        }
        try {
            FichajeRequestDTO fichajeRequestDTO = new FichajeRequestDTO(
                    empleadoDTO.getIdEmpleado(), tipoEventoFichaje, MetodoFichaje.MANUAL,
                    LocalDateTime.now(), null, null, UUID.randomUUID().toString(), null);

            controlHorarioEmpleadoService.registrarFichaje(fichajeRequestDTO);
            AlertUtils.mostrarConfirmacion("Confirmación", "Fichaje registrado correctamente");
            cargarEstado();
            cargarTablas();
        } catch (Exception e) {
            System.err.println("ERROR al registrar fichaje:");
            e.printStackTrace();
            AlertUtils.mostrarError("Error", "No se ha podido registrar el fichaje: " + e.getMessage());
        }
    }

    public void actualizarBotones(EstadoJornada estadoJornada) {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(true);
        btnSalida.setDisable(true);

        switch (estadoJornada) {
            case FUERA:
                btnEntrada.setDisable(false);
                enPausa = false;
                btnDescanso.setText("DESCANSO");
                break;
            case DENTRO:
                btnDescanso.setDisable(false);
                btnSalida.setDisable(false);
                enPausa = false;
                btnDescanso.setText("DESCANSO");
                break;
            case EN_PAUSA:
                btnDescanso.setDisable(false);
                enPausa = true;
                btnDescanso.setText("FIN DESCANSO");
                break;
        }
    }

    private void configurarTablas() {
        colFechaHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha()));
        colEntradaHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntrada()));
        colDescansoHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescanso()));
        colSalidaHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSalida()));
        colTipoHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoEvento()));
        colServidorHoy.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimeStampServidor()));

        colFechaHist.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestampServidor().format(fechaFormatter)));
        colTipoHist.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoEventoFichaje().toString()));
        colMetodoHist.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMetodoFichaje().toString()));
        colHoraHist.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestampServidor().format(horaFormatter)));
    }

    private void cargarEstado() {
        EmpleadoDTO empleado = SessionData.getInstance().getEmpleadoLogueado();
        if (empleado == null) return;
        try {
            EstadoJornada estadoJornada = controlHorarioEmpleadoService.obtenerEstado(empleado.getIdEmpleado());
            actualizarBotones(estadoJornada);
        } catch (Exception e) {
            System.err.println("ERROR al cargar estado:");
            e.printStackTrace();
            // AlertUtils.mostrarError("Error", "No se pudo obtener el estado de la jornada: " + e.getMessage());
        }
    }

    private void cargarTablas() {
        EmpleadoDTO empleado = SessionData.getInstance().getEmpleadoLogueado();
        if (empleado == null) return;
        try {
            int idEmpleado = empleado.getIdEmpleado();
            List<FichajeDTO> hoy = controlHorarioEmpleadoService.obtenerFichajesHoy(idEmpleado);
            List<FichajeDTO> historico = controlHorarioEmpleadoService.obtenerHistorico(idEmpleado);

            tablaHistorico.getItems().setAll(historico);

            FichajeHoyDTO fichajeHoyDTO = new FichajeHoyDTO();
            for (FichajeDTO fichaje : hoy) {
                String hora = fichaje.getTimestampServidor().format(horaFormatter);
                fichajeHoyDTO.setFecha(fichaje.getTimestampServidor().format(fechaFormatter));
                fichajeHoyDTO.setTipoEvento(fichaje.getTipoEventoFichaje().toString());
                fichajeHoyDTO.setTimeStampServidor(hora);

                switch (fichaje.getTipoEventoFichaje()) {
                    case ENTRADA -> fichajeHoyDTO.setEntrada(hora);
                    case PAUSA_INICIO -> fichajeHoyDTO.setDescanso(hora);
                    case SALIDA -> fichajeHoyDTO.setSalida(hora);
                    default -> {}
                }
            }
            tablaHoy.getItems().setAll(fichajeHoyDTO);
        } catch (Exception e) {
            System.err.println("ERROR al cargar tablas:");
            e.printStackTrace();
            // AlertUtils.mostrarError("Error", "No se pudieron cargar los fichajes: " + e.getMessage());
        }
    }

    private void iniciarActualizacionAutomatica() {
        timelineActualizacion = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            cargarEstado();
            cargarTablas();
        }));
        timelineActualizacion.setCycleCount(Timeline.INDEFINITE);
        timelineActualizacion.play();
    }

    private void iniciarContador() {
        timelineContador = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizarContador()));
        timelineContador.setCycleCount(Timeline.INDEFINITE);
        timelineContador.play();
    }

    private void actualizarContador() {
        EmpleadoDTO empleado = SessionData.getInstance().getEmpleadoLogueado();
        if (empleado == null) return;
        try {
            ContadorDTO contadorDTO = controlHorarioEmpleadoService.obtenerContador(empleado.getIdEmpleado());
            long segundos = contadorDTO.getSegundosTranscurridos();
            long horas = segundos / 3600;
            long minutos = (segundos % 3600) / 60;
            long segs = segundos % 60;
            contadorLabel.setText(String.format("%02d:%02d:%02d", horas, minutos, segs));
        } catch (Exception e) {
            contadorLabel.setText("--:--:--");
        }
    }

    private void actualizarHorasExtra() {
        EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();
        if (empleadoDTO == null) return;

        new Thread(() -> {
            try {
                HorasExtraDTO horasExtraDTO = controlHorarioEmpleadoService.obtenerHorasExtra(empleadoDTO.getIdEmpleado());
                Platform.runLater(() -> {
                    horasExtraLabel.setText(formatearHorasDecimales(horasExtraDTO.getHorasExtra()));
                    bolsaHorasLabel.setText(formatearHorasDecimales(horasExtraDTO.getSaldoHoras()));
                });
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }).start();
    }

    private void iniciarActualizacionHorasExtra() {
        actualizarHorasExtra();
        timelineHorasExtra = new Timeline(new KeyFrame(Duration.seconds(10), e -> actualizarHorasExtra()));
        timelineHorasExtra.setCycleCount(Timeline.INDEFINITE);
        timelineHorasExtra.play();
    }

    @FXML
    private void handleBack() {
        pararTimelines();
        ServicioNavegacion.getInstance().navigateToDashboardBase();
    }

    private void pararTimelines() {
        if (timelineContador != null) timelineContador.stop();
        if (timelineActualizacion != null) timelineActualizacion.stop();
        if (timelineHorasExtra != null) timelineHorasExtra.stop();
    }
    private String formatearHorasDecimales(double horas) {
        boolean negativo = horas < 0;
        double horasAbs = Math.abs(horas);
        long segundosTotales = Math.round(horasAbs * 3600);
        
        long hh = segundosTotales / 3600;
        long mm = (segundosTotales % 3600) / 60;
        long ss = segundosTotales % 60;
        
        String tiempo = String.format("%02d:%02d:%02d", hh, mm, ss);
        return negativo ? "-" + tiempo : tiempo;
    }
}
