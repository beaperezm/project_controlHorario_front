package com.proyectodam.fichappclient;

/**
 * Punto de entrada del JAR ejecutable.
 * maven-shade-plugin requiere una clase principal que NO extienda Application;
 * de lo contrario, JavaFX lanza un error al cargar el módulo desde el JAR sombreado.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
