package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response;

import com.danielgomez.franchise.domain.model.franchise.Branch;

import java.util.List;

/**
 * Sucursal con sus productos en las respuestas del API.
 */
public record BranchResponse(String id, String name, List<ProductResponse> products) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(branch.id(), branch.name(),
                branch.products().stream().map(ProductResponse::from).toList());
    }
}
