package com.danielgomez.franchise.domain.port.out;

import com.danielgomez.franchise.domain.model.franchise.Franchise;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para guardar y consultar franquicias. Lo implementa el adaptador de Mongo.
 */
public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String id);
}
