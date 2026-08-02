package io.github.ingcarlosgm.franchiseinventory.usecase.removeproduct;

import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RemoveProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Void> removeProduct(String productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("el producto", productId)))
                .flatMap(product -> productRepository.deleteById(product.getId()));
    }
}