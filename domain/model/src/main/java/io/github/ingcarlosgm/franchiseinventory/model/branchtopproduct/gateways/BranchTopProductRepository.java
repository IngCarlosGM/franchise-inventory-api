package io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.gateways;

import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.BranchTopProduct;
import reactor.core.publisher.Flux;

public interface BranchTopProductRepository {
    Flux<BranchTopProduct> findTopProductsByFranchiseId(String franchiseId);
}