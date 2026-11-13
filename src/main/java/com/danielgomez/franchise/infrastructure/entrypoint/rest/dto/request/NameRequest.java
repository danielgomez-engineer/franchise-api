package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Nombre para crear o renombrar una franquicia, sucursal o producto.
 */
public record NameRequest(@NotBlank String name) {
}
