package io.github.ingcarlosgm.franchiseinventory.api.branch;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;

public final class BranchApiMapper {

    private BranchApiMapper() {
    }

    public static Branch toDomain(CreateBranchRequest request, String franchiseId) {
        return Branch.builder()
                .franchiseId(franchiseId)
                .name(request.getName())
                .city(request.getCity())
                .phone(request.getPhone())
                .build();
    }

    public static BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
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