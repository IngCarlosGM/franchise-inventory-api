package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.FranchiseEntity;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class FranchiseAdapter implements FranchiseRepository {

    private final R2dbcEntityTemplate template;
    private final FranchiseDataRepository repository;

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        Instant now = Instant.now();
        FranchiseEntity entity = FranchiseMapper.toEntity(franchise);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return template.insert(entity).map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }
}