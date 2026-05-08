module com.proyectodam.fichappclient {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires transitive com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires jbcrypt;

    opens com.proyectodam.fichappclient to javafx.fxml;

    exports com.proyectodam.fichappclient;
    exports com.proyectodam.fichappclient.controller;
    exports com.proyectodam.fichappclient.model;
    exports com.proyectodam.fichappclient.config;
    exports com.proyectodam.fichappclient.enums;

    opens com.proyectodam.fichappclient.controller to javafx.fxml, javafx.base;
    opens com.proyectodam.fichappclient.controller.ControlHorario to javafx.fxml, javafx.base;
    opens com.proyectodam.fichappclient.model to com.fasterxml.jackson.databind;
    opens com.proyectodam.fichappclient.enums to com.fasterxml.jackson.databind;
    opens com.proyectodam.fichappclient.dto to com.fasterxml.jackson.databind;
    exports com.proyectodam.fichappclient.dto;
    exports com.proyectodam.fichappclient.service;
    exports com.proyectodam.fichappclient.util;
}