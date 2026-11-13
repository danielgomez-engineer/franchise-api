package com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response;

import com.danielgomez.franchise.domain.model.franchise.BranchTopProduct;

/**
 * Producto con más stock de una sucursal, con la sucursal a la que pertenece.
 */
public record TopStockProductResponse(String branchId, String branchName, String productId, String productName, int stock) {

    public static TopStockProductResponse from(BranchTopProduct top) {
        return new TopStockProductResponse(top.branchId(), top.branchName(),
                top.product().id(), top.product().name(), top.product().stock());
    }
}
