package io.github.ingcarlosgm.franchiseinventory.api.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private final String id;
    private final String branchId;
    private final String name;
    private final Integer stock;
    private final BigDecimal price;
    private final String unit;
    private final Instant createdAt;
    private final Instant updatedAt;
}