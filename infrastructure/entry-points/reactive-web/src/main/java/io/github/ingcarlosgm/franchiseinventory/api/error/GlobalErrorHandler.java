package io.github.ingcarlosgm.franchiseinventory.api.error;

import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    private final List<HttpMessageWriter<?>> messageWriters;

    public GlobalErrorHandler(ServerCodecConfigurer codecConfigurer) {
        this.messageWriters = codecConfigurer.getWriters();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (throwable instanceof ResourceNotFoundException ex) {
            log.warn("Recurso no encontrado: {}", ex.getMessage());
            return write(exchange, HttpStatus.NOT_FOUND, ErrorResponse.builder()
                    .error("RESOURCE_NOT_FOUND")
                    .message(ex.getMessage())
                    .build());
        }
        if (throwable instanceof InvalidDataException ex) {
            log.warn("Dato inválido: {}", ex.getMessage());
            return write(exchange, HttpStatus.BAD_REQUEST, ErrorResponse.builder()
                    .error("INVALID_DATA")
                    .message("Datos inválidos")
                    .errors(List.of(ErrorResponse.FieldError.builder()
                            .field(ex.getField())
                            .message(ex.getMessage())
                            .build()))
                    .build());
        }
        if (throwable instanceof DuplicateNameException ex) {
            log.warn("Nombre duplicado: {}", ex.getMessage());
            return write(exchange, HttpStatus.CONFLICT, ErrorResponse.builder()
                    .error("DUPLICATE_NAME")
                    .message(ex.getMessage())
                    .build());
        }
        if (throwable instanceof DecodingException) {
            log.warn("Cuerpo de petición mal formado");
            return write(exchange, HttpStatus.BAD_REQUEST, ErrorResponse.builder()
                    .error("INVALID_DATA")
                    .message("El cuerpo de la petición no es válido")
                    .build());
        }

        log.error("Error inesperado", throwable);
        return write(exchange, HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse.builder()
                .error("INTERNAL_ERROR")
                .message("Ocurrió un error inesperado")
                .build());
    }

    private Mono<Void> write(ServerWebExchange exchange, HttpStatus status, ErrorResponse body) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .flatMap(response -> response.writeTo(exchange, new ServerResponse.Context() {
                    @Override
                    public List<HttpMessageWriter<?>> messageWriters() {
                        return messageWriters;
                    }

                    @Override
                    public List<org.springframework.web.reactive.result.view.ViewResolver> viewResolvers() {
                        return List.of();
                    }
                }));
    }
}