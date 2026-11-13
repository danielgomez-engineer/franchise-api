package com.danielgomez.franchise.usecase.franchise;

import com.danielgomez.franchise.domain.model.exception.ResourceNotFoundException;
import com.danielgomez.franchise.domain.model.franchise.Branch;
import com.danielgomez.franchise.domain.model.franchise.Franchise;
import com.danielgomez.franchise.domain.model.franchise.Product;
import com.danielgomez.franchise.domain.port.out.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

/**
 * Casos de uso de franquicias, sucursales y productos.
 */
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public FranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Mono<Franchise> createFranchise(String name) {
        return Mono.fromCallable(() -> Franchise.create(name))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return update(franchiseId, franchise -> franchise.addBranch(Branch.create(branchName)));
    }

    public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, int stock) {
        return update(franchiseId, franchise -> franchise.addProduct(branchId, Product.create(productName, stock)));
    }

    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return update(franchiseId, franchise -> franchise.removeProduct(branchId, productId));
    }

    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, int stock) {
        return update(franchiseId, franchise -> franchise.updateProductStock(branchId, productId, stock));
    }

    public Mono<Franchise> renameFranchise(String franchiseId, String newName) {
        return update(franchiseId, franchise -> franchise.rename(newName));
    }

    public Mono<Franchise> renameBranch(String franchiseId, String branchId, String newName) {
        return update(franchiseId, franchise -> franchise.renameBranch(branchId, newName));
    }

    public Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String newName) {
        return update(franchiseId, franchise -> franchise.renameProduct(branchId, productId, newName));
    }

    private Mono<Franchise> update(String franchiseId, UnaryOperator<Franchise> change) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(() -> new ResourceNotFoundException("Franchise", franchiseId)))
                .map(change)
                .flatMap(franchiseRepository::save);
    }
}
