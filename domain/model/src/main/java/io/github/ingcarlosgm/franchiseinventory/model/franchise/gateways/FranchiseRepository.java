package io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> create(Franchise franchise);
    Mono<Boolean> existsByName(String name);
    Mono<Franchise> findById(String id);
}