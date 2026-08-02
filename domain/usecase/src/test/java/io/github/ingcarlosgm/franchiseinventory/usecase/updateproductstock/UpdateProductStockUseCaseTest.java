package io.github.ingcarlosgm.franchiseinventory.usecase.updateproductstock;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    private static final String PRODUCT_ID = "55555555-5555-5555-5555-555555555555";

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStockUseCase useCase;

    private Product existingProduct() {
        return Product.builder()
                .id(PRODUCT_ID).branchId("branch-1").name("Café").stock(40).build();
    }

    @Test
    void shouldReplaceStockWithTheReceivedValue() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct()));
        when(productRepository.update(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, 42))
                .expectNextMatches(product -> product.getStock() == 42
                        && "Café".equals(product.getName()))
                .verifyComplete();
    }

    @Test
    void shouldAcceptZeroStock() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct()));
        when(productRepository.update(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, 0))
                .expectNextMatches(product -> product.getStock() == 0)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct()));

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, -1))
                .expectError(InvalidDataException.class)
                .verify();

        verify(productRepository, never()).update(any(Product.class));
    }

    @Test
    void shouldFailWhenProductDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, 10))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(productRepository, never()).update(any(Product.class));
    }

    @Test
    void shouldBeIdempotentWhenAppliedTwice() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existingProduct()));
        when(productRepository.update(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, 42))
                .expectNextMatches(product -> product.getStock() == 42)
                .verifyComplete();

        StepVerifier.create(useCase.updateProductStock(PRODUCT_ID, 42))
                .expectNextMatches(product -> product.getStock() == 42)
                .verifyComplete();
    }
}