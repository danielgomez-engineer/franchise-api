package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.repository;

import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Repositorio reactivo de Spring Data para la colección franchises.
 */
public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {
}
