package io.github.ingcarlosgm.franchiseinventory.usecase.gettopproductsbyfranchise;

import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.BranchTopProduct;
import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.gateways.BranchTopProductRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopProductsByFranchiseUseCaseTest {

    private static final String FRANCHISE_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchTopProductRepository branchTopProductRepository;

    @InjectMocks
    private GetTopProductsByFranchiseUseCase useCase;

    private BranchTopProduct topProduct(String branchName, String productName, int stock) {
        return BranchTopProduct.builder()
                .branchId("branch-" + branchName)
                .branchName(branchName)
                .productId("product-" + productName)
                .productName(productName)
                .stock(stock)
                .build();
    }

    @Test
    void shouldEmitOneResultPerBranch() {
        when(franchiseRepository.findById(FRANCHISE_ID))
                .thenReturn(Mono.just(Franchise.builder().id(FRANCHISE_ID).name("Mi Franquicia").build()));
        when(branchTopProductRepository.findTopProductsByFranchiseId(FRANCHISE_ID))
                .thenReturn(Flux.just(
                        topProduct("Centro", "Café", 40),
                        topProduct("Norte", "Té", 25)));

        StepVerifier.create(useCase.getTopProductsByFranchise(FRANCHISE_ID))
                .expectNextMatches(result -> "Centro".equals(result.getBranchName())
                        && result.getStock() == 40)
                .expectNextMatches(result -> "Norte".equals(result.getBranchName())
                        && result.getStock() == 25)
                .verifyComplete();
    }

    @Test
    void shouldCompleteWithoutEmittingWhenFranchiseHasNoResults() {
        when(franchiseRepository.findById(FRANCHISE_ID))
                .thenReturn(Mono.just(Franchise.builder().id(FRANCHISE_ID).name("Mi Franquicia").build()));
        when(branchTopProductRepository.findTopProductsByFranchiseId(FRANCHISE_ID))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.getTopProductsByFranchise(FRANCHISE_ID))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenFranchiseDoesNotExist() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTopProductsByFranchise(FRANCHISE_ID))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(branchTopProductRepository, never()).findTopProductsByFranchiseId(anyString());
    }
}