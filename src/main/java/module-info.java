module com.proyectodam.fichappclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports com.proyectodam.fichappclient;
  //  exports com.proyectodam.fichappclient.controller;
   // exports com.proyectodam.fichappclient.model;
    opens com.proyectodam.fichappclient.controller to javafx.fxml;
    opens com.proyectodam.fichappclient.controller.ControlHorario to javafx.fxml;
    opens com.proyectodam.fichappclient.model to com.fasterxml.jackson.databind;
  //  exports com.proyectodam.fichappclient.controller.ControlHorario;
  //  opens com.proyectodam.fichappclient.controller.ControlHorario to javafx.fxml;
 //   exports com.proyectodam.fichappclient.util;
 //   opens com.proyectodam.fichappclient.util to javafx.fxml;
 //   exports com.proyectodam.fichappclient.util.controlhorario;
  //  opens com.proyectodam.fichappclient.util.controlhorario to javafx.fxml;
}