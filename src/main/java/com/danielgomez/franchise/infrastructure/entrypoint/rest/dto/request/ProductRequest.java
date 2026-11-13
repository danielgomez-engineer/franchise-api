package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos para agregar un producto a una sucursal.
 */
public record ProductRequest(@NotBlank String name, @NotNull @PositiveOrZero Integer stock) {
}
