package io.github.ingcarlosgm.franchiseinventory.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("franchise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseEntity {

    @Id
    private String id;

    private String name;

    @Column("contact_email")
    private String contactEmail;

    private String website;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}