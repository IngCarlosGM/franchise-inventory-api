package io.github.ingcarlosgm.franchiseinventory.api.product;

import io.github.ingcarlosgm.franchiseinventory.model.product.Product;

public final class ProductApiMapper {

    private ProductApiMapper() {
    }

    public static Product toDomain(CreateProductRequest request, String branchId) {
        return Product.builder()
                .branchId(branchId)
                .name(request.getName())
                .stock(request.getStock())
                .price(request.getPrice())
                .unit(request.getUnit())
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .branchId(product.getBranchId())
                .name(product.getName())
                .stock(product.getStock())
                .price(product.getPrice())
                .unit(product.getUnit())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}