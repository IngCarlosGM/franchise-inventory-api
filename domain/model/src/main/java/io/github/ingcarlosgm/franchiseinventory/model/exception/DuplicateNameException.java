package io.github.ingcarlosgm.franchiseinventory.model.exception;

import lombok.Getter;

@Getter
public class DuplicateNameException extends RuntimeException {
    private final String name;
    private final String scope;

    public DuplicateNameException(String name, String scope) {
        super("name %s ya existe en %s".formatted(name, scope));
        this.name = name;
        this.scope = scope;
    }
}