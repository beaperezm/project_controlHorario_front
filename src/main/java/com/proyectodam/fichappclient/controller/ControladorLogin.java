package com.proyectodam.fichappclient.controller;

import com.proyectodam.fichappclient.service.ServicioNavegacion;
import com.proyectodam.fichappclient.service.AccountNotActivatedException;
import com.proyectodam.fichappclient.model.EmpleadoDTO;
import com.proyectodam.fichappclient.util.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class ControladorLogin {

    @FXML
    private HBox titleBar;

    @FXML
    private TextField usernameField;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button activarCuentaButton;


    @FXML
    public void initialize() {
        // Solo mostrar el botón de activación si el modo de conexión es Supabase
        boolean esSupabase = com.proyectodam.fichappclient.util.AppConfig.getInstance().getModoActual() 
                == com.proyectodam.fichappclient.enums.ModoConexion.SUPABASE;
        activarCuentaButton.setVisible(esSupabase);
        activarCuentaButton.setManaged(esSupabase); // No ocupa espacio si está oculto

        // Configurar arrastre de ventana desde la barra de título
        if (titleBar != null) {
            titleBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, complete todos los campos.");
            errorLabel.setVisible(true);
        } else {
            try {
                com.proyectodam.fichappclient.service.AuthService authService = new com.proyectodam.fichappclient.service.AuthService();
                com.proyectodam.fichappclient.model.LoginResponseDTO response = authService.login(username, password);

                // Crear el DTO del empleado con los datos de la respuesta
                EmpleadoDTO usuario = new EmpleadoDTO();
                usuario.setIdEmpleado(response.getIdEmpleado());
                usuario.setEmail(username);
                usuario.setRol(response.getRole());
                
                // Nombre y apellidos reales que vienen en la respuesta de la API
                usuario.setNombre(response.getNombre() != null ? response.getNombre() : "Usuario");
                usuario.setApellidos(response.getApellidos() != null ? response.getApellidos() : "");

                // Guardar usuario en la sesión real
                SessionData.getInstance().setUsuarioLogueado(usuario);
                SessionData.getInstance().setIdEmpresa(response.getIdEmpresa());

                // GUARDAR EL TOKEN PARA LAS PETICIONES AL BACKEND
                com.proyectodam.fichappclient.util.SessionManager sm = com.proyectodam.fichappclient.util.SessionManager.getInstance();
                sm.setAccessToken(response.getAccessToken());
                sm.setRefreshToken(response.getRefreshToken());
                sm.setIdEmpleado(response.getIdEmpleado());
                sm.setRole(response.getRole());

                // Navegar al diseño principal
                ServicioNavegacion.getInstance().navigateToMainLayout("dashboard");

            } catch (AccountNotActivatedException e) {
                // Cuenta existe pero no está activada en Supabase → ofrecer activación
                mostrarDialogoActivacion(username);
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Credenciales inválidas o error de conexión.");
                errorLabel.setVisible(true);
            }
        }
    }

    /**
     * Muestra un diálogo para activar la cuenta del empleado en Supabase.
     * El empleado debe establecer una contraseña para su cuenta cloud.
     */
    private void mostrarDialogoActivacion(String email) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Activar Cuenta");
        dialog.setHeaderText("Tu cuenta necesita ser activada");

        // Contenido del diálogo
        VBox content = new VBox(12);
        content.setPadding(new Insets(20, 20, 10, 20));

        Label infoLabel = new Label(
            "Tu cuenta de empleado existe pero aún no ha sido activada\n" +
            "en el sistema de autenticación en la nube (Supabase).\n\n" +
            "Establece una contraseña para completar la activación:");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 13px;");

        Label emailLabel = new Label("Email: " + email);
        emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Nueva contraseña (mín. 6 caracteres)");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirmar contraseña");

        Label errorActivacion = new Label();
        errorActivacion.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
        errorActivacion.setVisible(false);
        errorActivacion.setWrapText(true);

        content.getChildren().addAll(infoLabel, emailLabel, passField, confirmField, errorActivacion);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(420);

        // Botones
        ButtonType activarButtonType = new ButtonType("Activar Cuenta", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(activarButtonType, ButtonType.CANCEL);

        // Deshabilitar el botón de activar hasta que se rellenen los campos
        Button activarBtn = (Button) dialog.getDialogPane().lookupButton(activarButtonType);
        activarBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold;");

        activarBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String pass = passField.getText();
            String confirm = confirmField.getText();

            if (pass.isEmpty() || confirm.isEmpty()) {
                errorActivacion.setText("Rellene ambos campos de contraseña.");
                errorActivacion.setVisible(true);
                event.consume(); // Evitar que se cierre el diálogo
                return;
            }
            if (pass.length() < 6) {
                errorActivacion.setText("La contraseña debe tener al menos 6 caracteres.");
                errorActivacion.setVisible(true);
                event.consume();
                return;
            }
            if (!pass.equals(confirm)) {
                errorActivacion.setText("Las contraseñas no coinciden.");
                errorActivacion.setVisible(true);
                event.consume();
                return;
            }

            // Intentar activar la cuenta
            try {
                com.proyectodam.fichappclient.service.AuthService authService = new com.proyectodam.fichappclient.service.AuthService();
                com.proyectodam.fichappclient.model.LoginResponseDTO response = authService.activarCuenta(email, pass);

                // Activación exitosa → hacer login automático
                EmpleadoDTO usuario = new EmpleadoDTO();
                usuario.setIdEmpleado(response.getIdEmpleado());
                usuario.setEmail(email);
                usuario.setRol(response.getRole());
                usuario.setNombre(response.getNombre() != null ? response.getNombre() : "Usuario");
                usuario.setApellidos(response.getApellidos() != null ? response.getApellidos() : "");

                SessionData.getInstance().setUsuarioLogueado(usuario);
                SessionData.getInstance().setIdEmpresa(response.getIdEmpresa());

                // GUARDAR EL TOKEN PARA LAS PETICIONES AL BACKEND
                com.proyectodam.fichappclient.util.SessionManager sm = com.proyectodam.fichappclient.util.SessionManager.getInstance();
                sm.setAccessToken(response.getAccessToken());
                sm.setRefreshToken(response.getRefreshToken());
                sm.setIdEmpleado(response.getIdEmpleado());
                sm.setRole(response.getRole());

                // Mostrar confirmación
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Cuenta Activada");
                exito.setHeaderText("¡Cuenta activada con éxito!");
                exito.setContentText("Tu cuenta ha sido vinculada a Supabase.\nA partir de ahora, usa esta contraseña para iniciar sesión.");
                exito.showAndWait();

                ServicioNavegacion.getInstance().navigateToMainLayout("dashboard");

            } catch (Exception ex) {
                ex.printStackTrace();
                errorActivacion.setText("Error al activar: " + ex.getMessage());
                errorActivacion.setVisible(true);
                event.consume(); // No cerrar el diálogo
            }
        });

        dialog.showAndWait();
    }

    /**
     * Handler del botón "¿Primera vez? Activar mi cuenta".
     * Abre el diálogo de activación con el email pre-rellenado.
     */
    @FXML
    private void handleActivarCuenta() {
        String email = usernameField.getText().trim();
        if (email.isEmpty()) {
            errorLabel.setText("Introduce tu email antes de activar la cuenta.");
            errorLabel.setVisible(true);
            return;
        }
        errorLabel.setVisible(false);
        mostrarDialogoActivacion(email);
    }

    @FXML
    private void handleCloseWindow() {
        System.exit(0);
    }
}
