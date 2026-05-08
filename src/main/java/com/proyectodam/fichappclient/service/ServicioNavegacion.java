package com.proyectodam.fichappclient.service;

import com.proyectodam.fichappclient.controller.EstructuraPrincipalController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Servicio Singleton que gestiona la navegación entre vistas de la aplicación.
 * Controla el cambio de escenas y la inyección de contenido en el área
 * principal.
 */
public class ServicioNavegacion {

    private static ServicioNavegacion instance;
    private StackPane contentArea;
    private Stage primaryStage;
    private EstructuraPrincipalController mainController;

    private ServicioNavegacion() {
    }

    public static ServicioNavegacion getInstance() {
        if (instance == null) {
            instance = new ServicioNavegacion();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/home-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass().getResource("/com/proyectodam/fichappclient/styles/main.css").toExternalForm());
            primaryStage.setScene(scene);
            com.proyectodam.fichappclient.util.WindowResizeHelper.addResizeListener(primaryStage);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateToMainLayout(String initialView) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/estructura-principal.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            // JavaFX no soporta @import en CSS, así que cargamos todos los módulos explícitamente
            String basePath = "/com/proyectodam/fichappclient/styles/";
            String[] cssModules = {
                "variables.css", "tipografia.css", "botones.css", "navegacion.css",
                "formularios.css", "contenedores.css", "tablas.css", "calendario.css",
                "utilidades.css", "dashboard_usuario.css"
            };
            for (String css : cssModules) {
                var resource = getClass().getResource(basePath + css);
                if (resource != null) {
                    scene.getStylesheets().add(resource.toExternalForm());
                }
            }
            primaryStage.setScene(scene);
            
            // Obtenemos el controlador ANTES del runLater para poder usarlo dentro del lambda
            this.mainController = loader.getController();
            
            // Al entrar al layout principal
            primaryStage.setMinHeight(750); // Aumentado para asegurar botones inferiores
            primaryStage.setMinWidth(1000);
            
            // Usamos Platform.runLater para evitar que en el render inicial de JavaFX
            // se quede la barra "cortada" por problemas de inicialización del layout.
            javafx.application.Platform.runLater(() -> {
                // Evitamos a toda costa llamar a primaryStage.setMaximized() porque en JavaFX 17+ 
                // con StageStyle.UNDECORATED provoca un bug de DWM en Windows que recorta la parte superior.
                // Aseguramos que el estado maximizado nativo esté en false.
                if (primaryStage.isMaximized()) {
                    primaryStage.setMaximized(false);
                }

                // Centrar manualmente el tamaño por defecto antes de decidir el rol
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

                // Determinamos el rol
                com.proyectodam.fichappclient.model.EmpleadoDTO usuario = 
                    com.proyectodam.fichappclient.util.SessionData.getInstance().getUsuarioLogueado();
                    
                if (usuario != null && "USER".equals(usuario.getRol())) {
                    // Modo ventana para usuario: Aumentamos el tamaño para evitar recortes
                    double targetWidth = 1100;
                    double targetHeight = 800;
                    primaryStage.setWidth(targetWidth);
                    primaryStage.setHeight(targetHeight);
                    double newX = screenBounds.getMinX() + (screenBounds.getWidth() - targetWidth) / 2;
                    double newY = screenBounds.getMinY() + (screenBounds.getHeight() - targetHeight) / 2;
                    primaryStage.setX(Math.max(newX, screenBounds.getMinX()));
                    primaryStage.setY(Math.max(newY, screenBounds.getMinY()));
                    
                    // Notificamos al controlador que la ventana NO está maximizada manualmente
                    if (this.mainController != null) {
                        this.mainController.setManualMaximized(false);
                    }
                } else {
                    // Modo pantalla completa manual para ADMIN/MANAGER
                    primaryStage.setX(screenBounds.getMinX());
                    primaryStage.setY(screenBounds.getMinY());
                    primaryStage.setWidth(screenBounds.getWidth());
                    primaryStage.setHeight(screenBounds.getHeight());
                    
                    // Notificamos al controlador que la ventana SÍ está maximizada manualmente
                    if (this.mainController != null) {
                        this.mainController.setManualMaximized(true);
                    }
                }
                
                // Segundo runLater: garantizar que la barra de título sea visible
                // después de que el layout se haya asentado completamente.
                javafx.application.Platform.runLater(() -> {
                    if (primaryStage.getY() < screenBounds.getMinY()) {
                        primaryStage.setY(screenBounds.getMinY());
                    }
                });
            });
            
            // Re-aplicar resizability helper después de cambiar de Scene
            com.proyectodam.fichappclient.util.WindowResizeHelper.addResizeListener(primaryStage);
            
            primaryStage.show();

            if (initialView != null && !initialView.isEmpty()) {
                switch (initialView) {
                    case "dashboard":
                        this.mainController.showDashboard();
                        break;
                    case "history":
                        this.mainController.showHistory();
                        break;
                    case "vacaciones":
                        this.mainController.showVacations();
                        break;
                    case "documents":
                        this.mainController.toggleDocuments();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
                
                // Sincronizar el estado del sidebar si el controlador principal está disponible
                if (mainController != null) {
                    mainController.syncSidebarState(fxmlPath);
                }
            } else {
                System.err.println(
                        "CRITICAL: El área de contenido (contentArea) es nula. No se puede navegar a: " + fxmlPath);
            }
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Fallo al cargar FXML: " + fxmlPath);
            System.err.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();
            com.proyectodam.fichappclient.util.AlertUtils.mostrarError("Error de Navegación", "No se pudo cargar la vista: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR INESPERADO al navegar a: " + fxmlPath);
            e.printStackTrace();
            com.proyectodam.fichappclient.util.AlertUtils.mostrarError("Error Inesperado", "Ocurrió un error al intentar abrir: " + fxmlPath);
        }
    }

    public void navigateToDashboardBase() {
        com.proyectodam.fichappclient.model.EmpleadoDTO u = 
            com.proyectodam.fichappclient.util.SessionData.getInstance().getUsuarioLogueado();
            
        if (u != null && "USER".equals(u.getRol())) {
            navigateTo("/com/proyectodam/fichappclient/views/user-dashboard.fxml");
        } else {
            navigateTo("/com/proyectodam/fichappclient/views/controlhorario/control-horario-empleador-empleado.fxml");
        }
    }

    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectodam/fichappclient/views/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            // Re-aplicar resizability helper después de cambiar de Scene
            com.proyectodam.fichappclient.util.WindowResizeHelper.addResizeListener(primaryStage);
            
            // Al cerrar sesión, ajustamos la ventana al tamaño del panel de login
            primaryStage.setMinHeight(100);
            primaryStage.setMinWidth(100);
            primaryStage.setMaximized(false);
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
