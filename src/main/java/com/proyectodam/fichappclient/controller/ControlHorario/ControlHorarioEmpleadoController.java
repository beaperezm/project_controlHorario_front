package com.proyectodam.fichappclient.controller.ControlHorario;

import com.proyectodam.fichappclient.enums.EstadoJornada;
import com.proyectodam.fichappclient.model.*;
import com.proyectodam.fichappclient.enums.MetodoFichaje;
import com.proyectodam.fichappclient.enums.TipoEventoFichaje;
import com.proyectodam.fichappclient.service.controlhorario.ControlHorarioEmpleadoService;
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
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ControlHorarioEmpleadoController {

    @FXML
    private Label nombreLabel, contadorLabel, horasExtraLabel, bolsaHorasLabel;
    @FXML
    private ImageView imgPerfil;
    @FXML
    private Button btnEntrada, btnDescanso, btnSalida;
    @FXML
    private TableView<FichajeHoyDTO> tablaHoy;
    @FXML
    private TableView<FichajeDTO> tablaHistorico;
    @FXML
    private TableColumn<FichajeHoyDTO, String> colFechaHoy, colEntradaHoy, colDescansoHoy, colSalidaHoy, colTipoHoy, colServidorHoy;
    @FXML
    private TableColumn<FichajeDTO, String> colFechaHist ,colTipoHist, colMetodoHist, colHoraHist;


    private final ControlHorarioEmpleadoService controlHorarioEmpleadoService = new ControlHorarioEmpleadoService();
    private Timeline timeline;

    private boolean enPausa = false;

    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {

        configurarTablas();
        tablaHoy.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaHistorico.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cargarEstado();
        cargarTablas();
        iniciarActualizacionAutomatica();
        iniciarContador();
        iniciarActualizacionHorasExtra();


        EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();

        if(empleadoDTO == null) {
            AlertUtils.mostrarError("Error", "No hay empleado logueado");
            return;
        }

        nombreLabel.setText(empleadoDTO.getNombre() + " " + empleadoDTO.getApellidos());

        if(empleadoDTO.getImgPerfil() != null) {
            imgPerfil.setImage(new Image(empleadoDTO.getImgPerfil()));
        }
    }

    private void estadoDentro() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(false);
        btnSalida.setDisable(false);
    }

    private void estadoEnPausa() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(false);
        btnSalida.setDisable(true);
    }

    private void desativarBotones() {
        btnEntrada.setDisable(true);
        btnDescanso.setDisable(true);
        btnSalida.setDisable(true);
    }

    @FXML
    private void ficharEntrada() {
        registrar(TipoEventoFichaje.ENTRADA);
        estadoDentro();
    }

    @FXML
    private void ficharSalida() {
        registrar(TipoEventoFichaje.SALIDA);
        desativarBotones();
    }

    @FXML
    private void ficharDescanso() {
        if(!enPausa) {
            registrar(TipoEventoFichaje.PAUSA_INICIO);
            enPausa = true;
            btnDescanso.setText("FIN DESCANSO");
            estadoEnPausa();
        } else {
            registrar(TipoEventoFichaje.PAUSA_FIN);
            enPausa = false;
            btnDescanso.setText("DESCANSO");
            estadoDentro();

        }
    }

    private void registrar(TipoEventoFichaje tipoEventoFichaje) {
        try {
            EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();

            FichajeRequestDTO fichajeRequestDTO = new FichajeRequestDTO(empleadoDTO.getIdEmpleado(), tipoEventoFichaje, MetodoFichaje.MANUAL, LocalDateTime.now(), null, null, UUID.randomUUID().toString(), null);

            controlHorarioEmpleadoService.registrarFichaje(fichajeRequestDTO);

            AlertUtils.mostrarConfirmacion("Confirmación", "Fichaje registrado correctamente");

            cargarEstado();
            cargarTablas();

        } catch (IOException | InterruptedException e) {
            AlertUtils.mostrarError("Error", "No se ha podido registrar el fichaje " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void  actualizarBotones(EstadoJornada estadoJornada) {
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
        try {
            int idEmpleado = SessionData.getInstance().getEmpleadoLogueado().getIdEmpleado();
            EstadoJornada estadoJornada = controlHorarioEmpleadoService.obtenerEstado(idEmpleado);
            actualizarBotones(estadoJornada);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarTablas() {
        try {
            int idEmpleado = SessionData.getInstance().getEmpleadoLogueado().getIdEmpleado();

            List<FichajeDTO> hoy = controlHorarioEmpleadoService.obtenerFichajesHoy(idEmpleado);
            List<FichajeDTO> historico = controlHorarioEmpleadoService.obtenerHistorico(idEmpleado);

            tablaHistorico.getItems().setAll(historico);

            FichajeHoyDTO fichajeHoyDTO = new FichajeHoyDTO();

            for(FichajeDTO fichaje: hoy) {
                String hora = fichaje.getTimestampServidor().format(horaFormatter);

                fichajeHoyDTO.setFecha(fichaje.getTimestampServidor().format(fechaFormatter));
                fichajeHoyDTO.setTipoEvento(fichaje.getTipoEventoFichaje().toString());
                fichajeHoyDTO.setTimeStampServidor(hora);

                switch (fichaje.getTipoEventoFichaje()) {
                    case ENTRADA:
                        fichajeHoyDTO.setEntrada(hora);
                        break;
                    case PAUSA_INICIO:
                        fichajeHoyDTO.setDescanso(hora);
                        break;
                    case SALIDA:
                        fichajeHoyDTO.setSalida(hora);
                        break;
                }
            }
            tablaHoy.getItems().setAll(fichajeHoyDTO);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void iniciarActualizacionAutomatica() {
        Timeline timeline = new Timeline(

            new KeyFrame(Duration.seconds(5), e -> {
                cargarEstado();
                cargarTablas();
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void iniciarContador() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    actualizarContador();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void actualizarContador() {
        try {
            int idEmpleado = SessionData.getInstance().getEmpleadoLogueado().getIdEmpleado();
            ContadorDTO contadorDTO = controlHorarioEmpleadoService.obtenerContador(idEmpleado);

            long segundos = contadorDTO.getSegundosTranscurridos();

            long horas = segundos / 3600;
            long minutos = (segundos % 3600) / 60;
            long segs = segundos % 60;

            contadorLabel.setText(String.format("%02d:%02d:%02d", horas, minutos, segs));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarHorasExtra() {

        EmpleadoDTO empleadoDTO = SessionData.getInstance().getEmpleadoLogueado();
        if(empleadoDTO == null) return;

        new Thread(() ->  {
            try {
                HorasExtraDTO horasExtraDTO = controlHorarioEmpleadoService.obtenerHorasExtra(empleadoDTO.getIdEmpleado());

                double bolsaHoras = controlHorarioEmpleadoService.obtenerBolsaHoras(empleadoDTO.getIdEmpleado());

                Platform.runLater(() -> {

                    horasExtraLabel.setText(formatearHorasExtra(horasExtraDTO.getHorasExtra()));

                    bolsaHorasLabel.setText(formatearHorasExtra(bolsaHoras));
                });


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void iniciarActualizacionHorasExtra() {

        actualizarHorasExtra();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> actualizarHorasExtra())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }

    private String formatearHorasExtra(double horas) {
        boolean negativo = horas < 0;
        horas =  Math.abs(horas);

        long segundos = Math.round(horas * 3600);

        long hh = segundos /3600;
        long mm = (segundos % 3600) / 60;
        long ss = segundos % 60;

        String tiempo = String.format("%02d:%02d:%02d", hh, mm, ss);

        return negativo ? "-" + tiempo : tiempo;
    }
}
