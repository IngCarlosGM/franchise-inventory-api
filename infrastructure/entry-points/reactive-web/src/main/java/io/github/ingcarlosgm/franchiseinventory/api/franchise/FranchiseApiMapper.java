package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;

public final class FranchiseApiMapper {

    private FranchiseApiMapper() {
    }

    public static Franchise toDomain(CreateFranchiseRequest request) {
        return Franchise.builder()
                .name(request.getName())
                .contactEmail(request.getContactEmail())
                .website(request.getWebsite())
                .build();
    }

    public static FranchiseResponse toResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .contactEmail(franchise.getContactEmail())
                .website(franchise.getWebsite())
                .createdAt(franchise.getCreatedAt())
                .updatedAt(franchise.getUpdatedAt())
                .build();
    }
}