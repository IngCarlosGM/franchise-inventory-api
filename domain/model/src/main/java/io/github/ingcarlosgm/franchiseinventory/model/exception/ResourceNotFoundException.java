package io.github.ingcarlosgm.franchiseinventory.model.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String id;

    public ResourceNotFoundException(String resource, String id) {
        super("No se encontró %s con id %s".formatted(resource, id));
        this.resource = resource;
        this.id = id;
    }
}