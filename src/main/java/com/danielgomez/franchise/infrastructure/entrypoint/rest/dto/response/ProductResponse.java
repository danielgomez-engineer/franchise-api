package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response;

import com.danielgomez.franchise.domain.model.franchise.Product;

/**
 * Producto en las respuestas del API.
 */
public record ProductResponse(String id, String name, int stock) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.name(), product.stock());
    }
}
