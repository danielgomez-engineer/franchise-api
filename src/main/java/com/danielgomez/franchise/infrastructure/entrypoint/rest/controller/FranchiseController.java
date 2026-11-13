package com.danielgomez.franchise.infrastructure.entrypoint.rest.controller;

import com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request.NameRequest;
import com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request.ProductRequest;
import com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.request.StockRequest;
import com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response.FranchiseResponse;
import com.danielgomez.franchise.infrastructure.entrypoint.rest.dto.response.TopStockProductResponse;
import com.danielgomez.franchise.usecase.franchise.FranchiseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Endpoints de franquicias, sucursales y productos.
 */
@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    public FranchiseController(FranchiseUseCase franchiseUseCase) {
        this.franchiseUseCase = franchiseUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> createFranchise(@Valid @RequestBody NameRequest request) {
        return franchiseUseCase.createFranchise(request.name())
                .map(FranchiseResponse::from);
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> addBranch(@PathVariable String franchiseId,
                                             @Valid @RequestBody NameRequest request) {
        return franchiseUseCase.addBranch(franchiseId, request.name())
                .map(FranchiseResponse::from);
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> addProduct(@PathVariable String franchiseId,
                                              @PathVariable String branchId,
                                              @Valid @RequestBody ProductRequest request) {
        return franchiseUseCase.addProduct(franchiseId, branchId, request.name(), request.stock())
                .map(FranchiseResponse::from);
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeProduct(@PathVariable String franchiseId,
                                    @PathVariable String branchId,
                                    @PathVariable String productId) {
        return franchiseUseCase.removeProduct(franchiseId, branchId, productId).then();
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<FranchiseResponse> updateProductStock(@PathVariable String franchiseId,
                                                      @PathVariable String branchId,
                                                      @PathVariable String productId,
                                                      @Valid @RequestBody StockRequest request) {
        return franchiseUseCase.updateProductStock(franchiseId, branchId, productId, request.stock())
                .map(FranchiseResponse::from);
    }

    @PatchMapping("/{franchiseId}")
    public Mono<FranchiseResponse> renameFranchise(@PathVariable String franchiseId,
                                                   @Valid @RequestBody NameRequest request) {
        return franchiseUseCase.renameFranchise(franchiseId, request.name())
                .map(FranchiseResponse::from);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}")
    public Mono<FranchiseResponse> renameBranch(@PathVariable String franchiseId,
                                                @PathVariable String branchId,
                                                @Valid @RequestBody NameRequest request) {
        return franchiseUseCase.renameBranch(franchiseId, branchId, request.name())
                .map(FranchiseResponse::from);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<FranchiseResponse> renameProduct(@PathVariable String franchiseId,
                                                 @PathVariable String branchId,
                                                 @PathVariable String productId,
                                                 @Valid @RequestBody NameRequest request) {
        return franchiseUseCase.renameProduct(franchiseId, branchId, productId, request.name())
                .map(FranchiseResponse::from);
    }

    @GetMapping("/{franchiseId}/top-stock-products")
    public Flux<TopStockProductResponse> getTopStockProductPerBranch(@PathVariable String franchiseId) {
        return franchiseUseCase.getTopStockProductPerBranch(franchiseId)
                .map(TopStockProductResponse::from);
    }
}
