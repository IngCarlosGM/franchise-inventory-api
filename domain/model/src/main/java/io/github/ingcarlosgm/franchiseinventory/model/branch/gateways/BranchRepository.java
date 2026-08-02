package io.github.ingcarlosgm.franchiseinventory.model.branch.gateways;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> create(Branch branch);
    Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name);
}