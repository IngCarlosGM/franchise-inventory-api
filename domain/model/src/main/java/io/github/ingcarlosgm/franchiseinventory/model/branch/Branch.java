package io.github.ingcarlosgm.franchiseinventory.model.branch;

import lombok.Builder;
import lombok.Getter;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import java.time.Instant;

@Getter
public class Branch {

    private final String id;
    private final String franchiseId;
    private final String name;
    private final String city;
    private final String phone;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Builder(toBuilder = true)
    public Branch(String id, String franchiseId, String name, String city, String phone,
                  Instant createdAt, Instant updatedAt) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("name", "El nombre no puede estar vacío");
        }
        this.id = id;
        this.franchiseId = franchiseId;
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}