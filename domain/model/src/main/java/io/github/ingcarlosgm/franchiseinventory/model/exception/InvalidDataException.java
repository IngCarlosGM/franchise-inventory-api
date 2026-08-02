package io.github.ingcarlosgm.franchiseinventory.model.exception;

import lombok.Getter;

@Getter
public class InvalidDataException extends RuntimeException {
    private final String field;

    public InvalidDataException(String field, String message) {
        super(message);
        this.field = field;
    }
}