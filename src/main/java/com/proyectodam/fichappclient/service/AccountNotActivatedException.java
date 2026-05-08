package com.proyectodam.fichappclient.service;

/**
 * Excepción lanzada cuando un empleado intenta hacer login
 * pero su cuenta aún no ha sido activada en Supabase Auth.
 */
public class AccountNotActivatedException extends RuntimeException {

    public AccountNotActivatedException(String message) {
        super(message);
    }
}
