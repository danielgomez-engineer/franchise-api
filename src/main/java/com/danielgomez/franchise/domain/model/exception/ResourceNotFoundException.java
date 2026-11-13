package com.danielgomez.franchise.domain.model.exception;

/**
 * Error cuando no se encuentra la franquicia, la sucursal o el producto.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s not found with id '%s'", resource, id));
    }
}
