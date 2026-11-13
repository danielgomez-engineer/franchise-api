package com.danielgomez.franchise.domain.model.franchise;

import com.danielgomez.franchise.domain.model.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Franquicia con sus sucursales. Los cambios a sucursales y productos se hacen desde aquí y cada uno devuelve una franquicia nueva.
 */
public record Franchise(String id, String name, List<Branch> branches) {

    public Franchise {
        id = Validations.requireId(id);
        name = Validations.requireName(name, "Franchise");
        branches = branches == null ? List.of() : List.copyOf(branches);
    }

    public static Franchise create(String name) {
        return new Franchise(UUID.randomUUID().toString(), name, List.of());
    }

    public Franchise rename(String newName) {
        return new Franchise(id, newName, branches);
    }

    public Franchise addBranch(Branch branch) {
        return withBranches(Stream.concat(branches.stream(), Stream.of(branch)).toList());
    }

    public Franchise renameBranch(String branchId, String newName) {
        return replaceBranch(branchId, branch -> branch.rename(newName));
    }

    public Franchise addProduct(String branchId, Product product) {
        return replaceBranch(branchId, branch -> branch.addProduct(product));
    }

    public Franchise removeProduct(String branchId, String productId) {
        return replaceBranch(branchId, branch -> branch.removeProduct(productId));
    }

    public Franchise updateProductStock(String branchId, String productId, int stock) {
        return replaceBranch(branchId, branch -> branch.updateProductStock(productId, stock));
    }

    public Franchise renameProduct(String branchId, String productId, String newName) {
        return replaceBranch(branchId, branch -> branch.renameProduct(productId, newName));
    }

    public Branch findBranch(String branchId) {
        return branches.stream()
                .filter(b -> b.id().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));
    }

    public List<BranchTopProduct> topStockProductPerBranch() {
        return branches.stream()
                .flatMap(branch -> branch.topStockProduct()
                        .map(product -> new BranchTopProduct(branch.id(), branch.name(), product))
                        .stream())
                .toList();
    }

    private Franchise replaceBranch(String branchId, UnaryOperator<Branch> change) {
        Branch updated = change.apply(findBranch(branchId));
        return withBranches(branches.stream().map(b -> b.id().equals(branchId) ? updated : b).toList());
    }

    private Franchise withBranches(List<Branch> newBranches) {
        return new Franchise(id, name, newBranches);
    }
}
