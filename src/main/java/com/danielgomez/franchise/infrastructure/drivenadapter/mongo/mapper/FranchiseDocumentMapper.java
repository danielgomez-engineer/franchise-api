package com.danielgomez.franchise.infrastructure.drivenadapter.mongo.mapper;

import com.danielgomez.franchise.domain.model.franchise.Branch;
import com.danielgomez.franchise.domain.model.franchise.Franchise;
import com.danielgomez.franchise.domain.model.franchise.Product;
import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document.BranchDocument;
import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document.FranchiseDocument;
import com.danielgomez.franchise.infrastructure.drivenadapter.mongo.document.ProductDocument;

import java.util.List;

/**
 * Convierte entre el modelo del dominio y los documentos de Mongo.
 */
public final class FranchiseDocumentMapper {

    private FranchiseDocumentMapper() {
    }

    public static FranchiseDocument toDocument(Franchise franchise) {
        List<BranchDocument> branches = franchise.branches().stream()
                .map(FranchiseDocumentMapper::toDocument)
                .toList();
        return new FranchiseDocument(franchise.id(), franchise.name(), branches);
    }

    public static Franchise toDomain(FranchiseDocument document) {
        List<Branch> branches = document.branches() == null ? List.of() : document.branches().stream()
                .map(FranchiseDocumentMapper::toDomain)
                .toList();
        return new Franchise(document.id(), document.name(), branches);
    }

    private static BranchDocument toDocument(Branch branch) {
        List<ProductDocument> products = branch.products().stream()
                .map(product -> new ProductDocument(product.id(), product.name(), product.stock()))
                .toList();
        return new BranchDocument(branch.id(), branch.name(), products);
    }

    private static Branch toDomain(BranchDocument document) {
        List<Product> products = document.products() == null ? List.of() : document.products().stream()
                .map(product -> new Product(product.id(), product.name(), product.stock()))
                .toList();
        return new Branch(document.id(), document.name(), products);
    }
}
