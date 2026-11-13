package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document;

/**
 * Producto guardado dentro de la sucursal.
 */
public record ProductDocument(String id, String name, int stock) {
}
