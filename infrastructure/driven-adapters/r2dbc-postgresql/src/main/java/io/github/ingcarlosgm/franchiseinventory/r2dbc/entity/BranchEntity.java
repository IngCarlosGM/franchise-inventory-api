package io.github.ingcarlosgm.franchiseinventory.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("branch")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {

    @Id
    private String id;

    @Column("franchise_id")
    private String franchiseId;

    private String name;
    private String city;
    private String phone;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}