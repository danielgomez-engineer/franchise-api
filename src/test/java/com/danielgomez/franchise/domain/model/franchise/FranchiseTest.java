package com.danielgomez.franchise.domain.model.franchise;

import com.danielgomez.franchise.domain.model.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class FranchiseTest {

    @Test
    void topStockPicksTheProductWithMostStockOfEachBranch() {
        Branch center = new Branch("b1", "Centro", List.of(
                new Product("p1", "Pan", 5),
                new Product("p2", "Leche", 9)));
        Branch north = new Branch("b2", "Norte", List.of(new Product("p3", "Queso", 2)));
        Franchise franchise = new Franchise("f1", "Panadería", List.of(center, north));

        assertThat(franchise.topStockProductPerBranch())
                .extracting(BranchTopProduct::branchName, top -> top.product().name())
                .containsExactly(
                        tuple("Centro", "Leche"),
                        tuple("Norte", "Queso"));
    }

    @Test
    void topStockSkipsBranchesWithoutProducts() {
        Franchise franchise = new Franchise("f1", "Panadería", List.of(new Branch("b1", "Centro", List.of())));

        assertThat(franchise.topStockProductPerBranch()).isEmpty();
    }

    @Test
    void topStockOnATieKeepsTheFirstProductAdded() {
        Branch center = new Branch("b1", "Centro", List.of(
                new Product("p1", "Pan", 7),
                new Product("p2", "Leche", 7)));
        Franchise franchise = new Franchise("f1", "Panadería", List.of(center));

        assertThat(franchise.topStockProductPerBranch().get(0).product().name()).isEqualTo("Pan");
    }

    @Test
    void changesReturnANewFranchiseAndKeepTheOriginal() {
        Franchise original = Franchise.create("Panadería");

        Franchise renamed = original.rename("Panadería Central");

        assertThat(original.name()).isEqualTo("Panadería");
        assertThat(renamed.name()).isEqualTo("Panadería Central");
        assertThat(renamed.id()).isEqualTo(original.id());
    }

    @Test
    void productWithNegativeStockIsRejected() {
        assertThatThrownBy(() -> Product.create("Pan", -1))
                .isInstanceOf(BusinessException.class);
    }
}
