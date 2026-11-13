package com.danielgomez.franchise.usecase.franchise;

import com.danielgomez.franchise.domain.model.exception.BusinessException;
import com.danielgomez.franchise.domain.model.exception.ResourceNotFoundException;
import com.danielgomez.franchise.domain.model.franchise.Branch;
import com.danielgomez.franchise.domain.model.franchise.Franchise;
import com.danielgomez.franchise.domain.model.franchise.Product;
import com.danielgomez.franchise.domain.port.out.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    private Product bread;
    private Branch center;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        bread = new Product("p1", "Pan", 5);
        center = new Branch("b1", "Centro", List.of(bread));
        franchise = new Franchise("f1", "Panadería", List.of(center));

        when(franchiseRepository.findById("f1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findById("no-existe")).thenReturn(Mono.empty());
        when(franchiseRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    void createFranchiseSavesIt() {
        StepVerifier.create(franchiseUseCase.createFranchise("  Nueva  "))
                .assertNext(created -> {
                    assertThat(created.id()).isNotBlank();
                    assertThat(created.name()).isEqualTo("Nueva");
                    assertThat(created.branches()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void createFranchiseWithBlankNameFails() {
        StepVerifier.create(franchiseUseCase.createFranchise(" "))
                .expectError(BusinessException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void addBranchAddsItToTheFranchise() {
        StepVerifier.create(franchiseUseCase.addBranch("f1", "Norte"))
                .assertNext(updated -> assertThat(updated.branches())
                        .extracting(Branch::name)
                        .containsExactly("Centro", "Norte"))
                .verifyComplete();
    }

    @Test
    void addBranchToMissingFranchiseFails() {
        StepVerifier.create(franchiseUseCase.addBranch("no-existe", "Norte"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void addProductAddsItToTheBranch() {
        StepVerifier.create(franchiseUseCase.addProduct("f1", "b1", "Leche", 9))
                .assertNext(updated -> assertThat(updated.findBranch("b1").products())
                        .extracting(Product::name, Product::stock)
                        .containsExactly(
                                tuple("Pan", 5),
                                tuple("Leche", 9)))
                .verifyComplete();
    }

    @Test
    void addProductToMissingBranchFails() {
        StepVerifier.create(franchiseUseCase.addProduct("f1", "no-existe", "Leche", 9))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void removeProductRemovesItFromTheBranch() {
        StepVerifier.create(franchiseUseCase.removeProduct("f1", "b1", "p1"))
                .assertNext(updated -> assertThat(updated.findBranch("b1").products()).isEmpty())
                .verifyComplete();
    }

    @Test
    void removeMissingProductFails() {
        StepVerifier.create(franchiseUseCase.removeProduct("f1", "b1", "no-existe"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void updateProductStockChangesTheStock() {
        StepVerifier.create(franchiseUseCase.updateProductStock("f1", "b1", "p1", 20))
                .assertNext(updated -> assertThat(updated.findBranch("b1").findProduct("p1").stock()).isEqualTo(20))
                .verifyComplete();
    }

    @Test
    void updateProductStockWithNegativeValueFails() {
        StepVerifier.create(franchiseUseCase.updateProductStock("f1", "b1", "p1", -1))
                .expectError(BusinessException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void renameFranchiseChangesItsName() {
        StepVerifier.create(franchiseUseCase.renameFranchise("f1", "Panadería Central"))
                .assertNext(updated -> assertThat(updated.name()).isEqualTo("Panadería Central"))
                .verifyComplete();
    }

    @Test
    void renameBranchChangesItsName() {
        StepVerifier.create(franchiseUseCase.renameBranch("f1", "b1", "Sur"))
                .assertNext(updated -> assertThat(updated.findBranch("b1").name()).isEqualTo("Sur"))
                .verifyComplete();
    }

    @Test
    void renameProductChangesItsName() {
        StepVerifier.create(franchiseUseCase.renameProduct("f1", "b1", "p1", "Pan integral"))
                .assertNext(updated -> assertThat(updated.findBranch("b1").findProduct("p1").name()).isEqualTo("Pan integral"))
                .verifyComplete();
    }

    @Test
    void topStockReturnsOneProductPerBranchWithProducts() {
        Branch north = new Branch("b2", "Norte", List.of(
                new Product("p2", "Leche", 3),
                new Product("p3", "Queso", 12)));
        Branch empty = new Branch("b3", "Vacía", List.of());
        Franchise withBranches = new Franchise("f2", "Lácteos", List.of(center, north, empty));
        when(franchiseRepository.findById("f2")).thenReturn(Mono.just(withBranches));

        StepVerifier.create(franchiseUseCase.getTopStockProductPerBranch("f2"))
                .assertNext(top -> {
                    assertThat(top.branchName()).isEqualTo("Centro");
                    assertThat(top.product().name()).isEqualTo("Pan");
                })
                .assertNext(top -> {
                    assertThat(top.branchName()).isEqualTo("Norte");
                    assertThat(top.product().name()).isEqualTo("Queso");
                })
                .verifyComplete();
    }

    @Test
    void topStockOfMissingFranchiseFails() {
        StepVerifier.create(franchiseUseCase.getTopStockProductPerBranch("no-existe"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void renameFranchiseWithBlankNameFails() {
        StepVerifier.create(franchiseUseCase.renameFranchise("f1", " "))
                .expectError(BusinessException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void updateStockOfMissingProductFails() {
        StepVerifier.create(franchiseUseCase.updateProductStock("f1", "b1", "no-existe", 10))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void topStockOfFranchiseWithoutBranchesIsEmpty() {
        when(franchiseRepository.findById("f3")).thenReturn(Mono.just(new Franchise("f3", "Sin sucursales", List.of())));

        StepVerifier.create(franchiseUseCase.getTopStockProductPerBranch("f3"))
                .verifyComplete();
    }

    @Test
    void repositoryErrorIsPropagated() {
        doReturn(Mono.error(new RuntimeException("db down"))).when(franchiseRepository).save(any());

        StepVerifier.create(franchiseUseCase.addBranch("f1", "Norte"))
                .expectErrorMessage("db down")
                .verify();
    }
}
