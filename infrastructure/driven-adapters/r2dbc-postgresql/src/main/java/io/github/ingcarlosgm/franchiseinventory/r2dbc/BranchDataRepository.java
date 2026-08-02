package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BranchDataRepository extends ReactiveCrudRepository<BranchEntity, String> {
    Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name);
}