package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Franquicia guardada en Mongo. Las sucursales y sus productos van dentro del mismo documento.
 */
@Document(collection = "franchises")
public record FranchiseDocument(@Id String id, String name, List<BranchDocument> branches) {
}
