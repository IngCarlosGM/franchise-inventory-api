package io.github.ingcarlosgm.franchiseinventory.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    private String id;

    @Column("branch_id")
    private String branchId;

    private String name;
    private Integer stock;
    private BigDecimal price;
    private String unit;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}