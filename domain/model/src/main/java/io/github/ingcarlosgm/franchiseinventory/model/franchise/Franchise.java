package io.github.ingcarlosgm.franchiseinventory.model.franchise;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;

@Getter
public class Franchise {

    private final String id;
    private final String name;
    private final String contactEmail;
    private final String website;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Builder(toBuilder = true)
    public Franchise(String id, String name, String contactEmail, String website,
                     Instant createdAt, Instant updatedAt) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("name", "El nombre no puede estar vacío");
        }
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.website = website;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}