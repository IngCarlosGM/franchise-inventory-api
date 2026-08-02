package io.github.ingcarlosgm.franchiseinventory.r2dbc.mapper;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.r2dbc.entity.FranchiseEntity;

public final class FranchiseMapper {

    private FranchiseMapper() {
    }

    public static Franchise toDomain(FranchiseEntity entity) {
        return Franchise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contactEmail(entity.getContactEmail())
                .website(entity.getWebsite())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static FranchiseEntity toEntity(Franchise franchise) {
        return FranchiseEntity.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .contactEmail(franchise.getContactEmail())
                .website(franchise.getWebsite())
                .createdAt(franchise.getCreatedAt())
                .updatedAt(franchise.getUpdatedAt())
                .build();
    }
}