package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Nuevo stock de un producto.
 */
public record StockRequest(@NotNull @PositiveOrZero Integer stock) {
}
