package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.adapter;

import com.danielgomez.franchise.domain.model.franchise.Franchise;
import com.danielgomez.franchise.domain.port.out.FranchiseRepository;
import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.mapper.FranchiseDocumentMapper;
import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.repository.FranchiseMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implementa el puerto FranchiseRepository usando Mongo.
 */
@Repository
public class FranchiseMongoAdapter implements FranchiseRepository {

    private final FranchiseMongoRepository mongoRepository;

    public FranchiseMongoAdapter(FranchiseMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return mongoRepository.save(FranchiseDocumentMapper.toDocument(franchise))
                .map(FranchiseDocumentMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepository.findById(id)
                .map(FranchiseDocumentMapper::toDomain);
    }
}
