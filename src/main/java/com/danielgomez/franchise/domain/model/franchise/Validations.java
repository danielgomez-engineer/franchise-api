package com.danielgomez.franchise.domain.model.franchise;

import com.danielgomez.franchise.domain.model.exception.BusinessException;

/**
 * Validaciones que comparten Franchise, Branch y Product.
 */
final class Validations {

    private Validations() {
    }

    static String requireId(String id) {
        if (id == null || id.isBlank()) {
            throw new BusinessException("Id is required");
        }
        return id;
    }

    static String requireName(String name, String resource) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(resource + " name is required");
        }
        return name.trim();
    }
}
