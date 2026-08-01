package io.github.ingcarlosgm.franchiseinventory.model.product;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class Product {

    private final String id;
    private final String branchId;
    private final String name;
    private final Integer stock;
    private final BigDecimal price;
    private final String unit;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Builder(toBuilder = true)
    public Product(String id, String branchId, String name, Integer stock, BigDecimal price,
                   String unit, Instant createdAt, Instant updatedAt) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("name", "El nombre no puede estar vacío");
        }
        if (stock == null || stock < 0) {
            throw new InvalidDataException("stock", "El stock no puede ser negativo");
        }
        if (price != null && price.signum() < 0) {
            throw new InvalidDataException("price", "El precio no puede ser negativo");
        }
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}