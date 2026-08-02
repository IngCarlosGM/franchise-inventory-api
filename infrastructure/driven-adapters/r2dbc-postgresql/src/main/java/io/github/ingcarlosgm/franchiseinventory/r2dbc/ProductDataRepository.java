package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductDataRepository
        extends ReactiveCrudRepository<ProductEntity, String> {

    Mono<Boolean> existsByBranchIdAndName(String branchId, String name);
}