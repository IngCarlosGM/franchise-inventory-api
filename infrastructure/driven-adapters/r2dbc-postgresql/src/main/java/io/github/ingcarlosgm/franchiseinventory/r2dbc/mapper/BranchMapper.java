package io.github.ingcarlosgm.franchiseinventory.r2dbc.mapper;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.BranchEntity;

public final class BranchMapper {

    private BranchMapper() {
    }

    public static Branch toDomain(BranchEntity entity) {
        return Branch.builder()
                .id(entity.getId())
                .franchiseId(entity.getFranchiseId())
                .name(entity.getName())
                .city(entity.getCity())
                .phone(entity.getPhone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static BranchEntity toEntity(Branch branch) {
        return BranchEntity.builder()
                .id(branch.getId())
                .franchiseId(branch.getFranchiseId())
                .name(branch.getName())
                .city(branch.getCity())
                .phone(branch.getPhone())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}