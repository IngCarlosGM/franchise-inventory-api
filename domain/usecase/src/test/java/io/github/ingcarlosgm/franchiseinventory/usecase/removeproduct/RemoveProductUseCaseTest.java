package io.github.ingcarlosgm.franchiseinventory.usecase.removeproduct;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveProductUseCaseTest {

    private static final String PRODUCT_ID = "55555555-5555-5555-5555-555555555555";

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RemoveProductUseCase useCase;

    @Test
    void shouldCompleteWithoutEmittingWhenProductIsRemoved() {
        Product existing = Product.builder()
                .id(PRODUCT_ID).name("Café").stock(40).build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.just(existing));
        when(productRepository.deleteById(PRODUCT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.removeProduct(PRODUCT_ID))
                .verifyComplete();

        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @Test
    void shouldFailWhenProductDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.removeProduct(PRODUCT_ID))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(productRepository, never()).deleteById(anyString());
    }
}