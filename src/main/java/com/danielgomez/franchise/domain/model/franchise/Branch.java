package com.danielgomez.franchise.domain.model.franchise;

import com.danielgomez.franchise.domain.model.exception.ResourceNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Sucursal de una franquicia con sus productos.
 */
public record Branch(String id, String name, List<Product> products) {

    public Branch {
        id = Validations.requireId(id);
        name = Validations.requireName(name, "Branch");
        products = products == null ? List.of() : List.copyOf(products);
    }

    public static Branch create(String name) {
        return new Branch(UUID.randomUUID().toString(), name, List.of());
    }

    public Branch rename(String newName) {
        return new Branch(id, newName, products);
    }

    public Branch addProduct(Product product) {
        return withProducts(Stream.concat(products.stream(), Stream.of(product)).toList());
    }

    public Branch removeProduct(String productId) {
        findProduct(productId);
        return withProducts(products.stream().filter(p -> !p.id().equals(productId)).toList());
    }

    public Branch updateProductStock(String productId, int stock) {
        return replaceProduct(productId, product -> product.withStock(stock));
    }

    public Branch renameProduct(String productId, String newName) {
        return replaceProduct(productId, product -> product.rename(newName));
    }

    public Product findProduct(String productId) {
        return products.stream()
                .filter(p -> p.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    public Optional<Product> topStockProduct() {
        return products.stream().max(Comparator.comparingInt(Product::stock));
    }

    private Branch replaceProduct(String productId, UnaryOperator<Product> change) {
        Product updated = change.apply(findProduct(productId));
        return withProducts(products.stream().map(p -> p.id().equals(productId) ? updated : p).toList());
    }

    private Branch withProducts(List<Product> newProducts) {
        return new Branch(id, name, newProducts);
    }
}
