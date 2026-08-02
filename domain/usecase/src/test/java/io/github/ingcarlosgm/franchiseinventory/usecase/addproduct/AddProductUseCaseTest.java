package io.github.ingcarlosgm.franchiseinventory.usecase.addproduct;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.branch.gateways.BranchRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddProductUseCaseTest {

    private static final String BRANCH_ID = "22222222-2222-2222-2222-222222222222";
    private static final String OTHER_BRANCH_ID = "33333333-3333-3333-3333-333333333333";
    private static final String GENERATED_ID = "55555555-5555-5555-5555-555555555555";

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IdentityGenerator identityGenerator;

    @InjectMocks
    private AddProductUseCase useCase;

    private Branch existingBranch(String id) {
        return Branch.builder().id(id).name("Centro").build();
    }

    @Test
    void shouldAddProductWhenBranchExistsAndNameIsAvailable() {
        Product input = Product.builder().name("Café").stock(40).branchId(BRANCH_ID).build();
        Product persisted = input.toBuilder().id(GENERATED_ID).build();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch(BRANCH_ID)));
        when(productRepository.existsByBranchIdAndName(BRANCH_ID, "Café")).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(productRepository.create(any(Product.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addProduct(input))
                .expectNextMatches(product -> GENERATED_ID.equals(product.getId())
                        && BRANCH_ID.equals(product.getBranchId())
                        && product.getStock() == 40)
                .verifyComplete();
    }

    @Test
    void shouldAddProductWithOptionalAttributes() {
        Product input = Product.builder()
                .name("Café").stock(40).branchId(BRANCH_ID)
                .price(new BigDecimal("18500.00")).unit("unidades")
                .build();
        Product persisted = input.toBuilder().id(GENERATED_ID).build();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch(BRANCH_ID)));
        when(productRepository.existsByBranchIdAndName(anyString(), anyString())).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(productRepository.create(any(Product.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addProduct(input))
                .expectNextMatches(product -> new BigDecimal("18500.00").equals(product.getPrice())
                        && "unidades".equals(product.getUnit()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBranchDoesNotExist() {
        Product input = Product.builder().name("Café").stock(40).branchId(BRANCH_ID).build();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.empty());
        when(productRepository.existsByBranchIdAndName(anyString(), anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.addProduct(input))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(productRepository, never()).create(any(Product.class));
    }

    @Test
    void shouldFailWhenNameIsAlreadyUsedInTheSameBranch() {
        Product input = Product.builder().name("Café").stock(40).branchId(BRANCH_ID).build();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch(BRANCH_ID)));
        when(productRepository.existsByBranchIdAndName(BRANCH_ID, "Café")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.addProduct(input))
                .expectError(DuplicateNameException.class)
                .verify();

        verify(productRepository, never()).create(any(Product.class));
    }

    @Test
    void shouldAllowSameNameInAnotherBranch() {
        Product input = Product.builder().name("Café").stock(40).branchId(OTHER_BRANCH_ID).build();
        Product persisted = input.toBuilder().id(GENERATED_ID).build();

        when(branchRepository.findById(OTHER_BRANCH_ID)).thenReturn(Mono.just(existingBranch(OTHER_BRANCH_ID)));
        when(productRepository.existsByBranchIdAndName(OTHER_BRANCH_ID, "Café")).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(productRepository.create(any(Product.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addProduct(input))
                .expectNextMatches(product -> OTHER_BRANCH_ID.equals(product.getBranchId()))
                .verifyComplete();
    }

    @Test
    void shouldAcceptZeroStock() {
        Product input = Product.builder().name("Café").stock(0).branchId(BRANCH_ID).build();
        Product persisted = input.toBuilder().id(GENERATED_ID).build();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Mono.just(existingBranch(BRANCH_ID)));
        when(productRepository.existsByBranchIdAndName(anyString(), anyString())).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(productRepository.create(any(Product.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addProduct(input))
                .expectNextMatches(product -> product.getStock() == 0)
                .verifyComplete();
    }
}