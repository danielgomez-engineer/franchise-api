package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response;

import com.danielgomez.franchise.domain.model.franchise.Franchise;

import java.util.List;

/**
 * Franquicia con sus sucursales en las respuestas del API.
 */
public record FranchiseResponse(String id, String name, List<BranchResponse> branches) {

    public static FranchiseResponse from(Franchise franchise) {
        return new FranchiseResponse(franchise.id(), franchise.name(),
                franchise.branches().stream().map(BranchResponse::from).toList());
    }
}
