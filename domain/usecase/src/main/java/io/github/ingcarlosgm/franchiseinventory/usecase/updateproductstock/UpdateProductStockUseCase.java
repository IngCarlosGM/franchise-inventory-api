package io.github.ingcarlosgm.franchiseinventory.usecase.updateproductstock;

import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> updateProductStock(String productId, Integer stock) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("el producto", productId)))
                .map(product -> product.toBuilder().stock(stock).build())
                .flatMap(productRepository::update);
    }
}