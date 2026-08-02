package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.branch.gateways.BranchRepository;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.BranchEntity;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.mapper.BranchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class BranchAdapter implements BranchRepository {

    private final R2dbcEntityTemplate template;
    private final BranchDataRepository repository;

    @Override
    public Mono<Branch> create(Branch branch) {
        Instant now = Instant.now();
        BranchEntity entity = BranchMapper.toEntity(branch);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return template.insert(entity).map(BranchMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name) {
        return repository.existsByFranchiseIdAndName(franchiseId, name);
    }
}