package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.BranchTopProduct;

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

    public static BranchTopProductResponse toResponse(BranchTopProduct topProduct) {
        return BranchTopProductResponse.builder()
                .branchId(topProduct.getBranchId())
                .branchName(topProduct.getBranchName())
                .productId(topProduct.getProductId())
                .productName(topProduct.getProductName())
                .stock(topProduct.getStock())
                .build();
    }
}