package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document;

import java.util.List;

/**
 * Sucursal guardada dentro del documento de la franquicia.
 */
public record BranchDocument(String id, String name, List<ProductDocument> products) {
}
