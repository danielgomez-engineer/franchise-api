package com.danielgomez.franchise.domain.model.exception;

/**
 * Error cuando no se cumple una regla del negocio, por ejemplo stock negativo o nombre vacío.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
