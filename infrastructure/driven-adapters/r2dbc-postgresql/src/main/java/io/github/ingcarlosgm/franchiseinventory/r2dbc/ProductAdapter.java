package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.ProductEntity;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class ProductAdapter implements ProductRepository {

    private final R2dbcEntityTemplate template;
    private final ProductDataRepository repository;

    @Override
    public Mono<Product> create(Product product) {
        Instant now = Instant.now();
        ProductEntity entity = ProductMapper.toEntity(product);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return template.insert(entity).map(ProductMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByBranchIdAndName(String branchId, String name) {
        return repository.existsByBranchIdAndName(branchId, name);
    }
}