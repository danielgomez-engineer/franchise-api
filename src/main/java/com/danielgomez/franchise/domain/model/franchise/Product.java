package com.danielgomez.franchise.domain.model.franchise;

import com.danielgomez.franchise.domain.model.exception.BusinessException;

import java.util.UUID;

/**
 * Producto de una sucursal con su stock.
 */
public record Product(String id, String name, int stock) {

    public Product {
        id = Validations.requireId(id);
        name = Validations.requireName(name, "Product");
        if (stock < 0) {
            throw new BusinessException("Stock cannot be negative");
        }
    }

    public static Product create(String name, int stock) {
        return new Product(UUID.randomUUID().toString(), name, stock);
    }

    public Product withStock(int newStock) {
        return new Product(id, name, newStock);
    }

    public Product rename(String newName) {
        return new Product(id, newName, stock);
    }
}
