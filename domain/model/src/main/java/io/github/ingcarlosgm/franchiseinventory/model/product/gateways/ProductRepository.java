package io.github.ingcarlosgm.franchiseinventory.model.product.gateways;

import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> create(Product product);
    Mono<Boolean> existsByBranchIdAndName(String branchId, String name);
    Mono<Product> findById(String id);
    Mono<Void> deleteById(String id);
}