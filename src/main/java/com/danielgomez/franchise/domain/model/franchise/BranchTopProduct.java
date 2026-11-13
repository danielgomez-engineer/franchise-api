package com.danielgomez.franchise.domain.model.franchise;

/**
 * Producto con más stock de una sucursal, junto con la sucursal a la que pertenece.
 */
public record BranchTopProduct(String branchId, String branchName, Product product) {
}
