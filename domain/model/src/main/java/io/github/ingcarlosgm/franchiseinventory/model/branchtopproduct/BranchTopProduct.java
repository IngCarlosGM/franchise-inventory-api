package io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchTopProduct {
    private final String branchId;
    private final String branchName;
    private final String productId;
    private final String productName;
    private final Integer stock;
}