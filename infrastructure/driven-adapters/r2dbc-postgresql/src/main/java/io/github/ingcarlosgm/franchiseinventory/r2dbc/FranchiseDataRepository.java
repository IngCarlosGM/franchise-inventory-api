package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FranchiseDataRepository extends ReactiveCrudRepository<FranchiseEntity, String> {
    Mono<Boolean> existsByName(String name);
}