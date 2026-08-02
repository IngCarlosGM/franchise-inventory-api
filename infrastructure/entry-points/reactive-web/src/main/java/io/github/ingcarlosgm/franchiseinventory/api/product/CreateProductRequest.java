package io.github.ingcarlosgm.franchiseinventory.api.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private String name;
    private Integer stock;
    private BigDecimal price;
    private String unit;
}