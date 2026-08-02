package io.github.ingcarlosgm.franchiseinventory.api.error;

import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.InvalidDataException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalErrorHandlerTest {

    private GlobalErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalErrorHandler(ServerCodecConfigurer.create());
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() {
        MockServerWebExchange exchange = exchange();

        StepVerifier.create(handler.handle(exchange,
                        new ResourceNotFoundException("la franquicia", "abc-123")))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenDataIsInvalid() {
        MockServerWebExchange exchange = exchange();

        StepVerifier.create(handler.handle(exchange,
                        new InvalidDataException("name", "El nombre no puede estar vacío")))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturnConflictWhenNameIsDuplicated() {
        MockServerWebExchange exchange = exchange();

        StepVerifier.create(handler.handle(exchange,
                        new DuplicateNameException("Centro", "la franquicia abc-123")))
                .verifyComplete();

        assertEquals(HttpStatus.CONFLICT, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() {
        MockServerWebExchange exchange = exchange();

        StepVerifier.create(handler.handle(exchange, new DecodingException("bad json")))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedFailures() {
        MockServerWebExchange exchange = exchange();

        StepVerifier.create(handler.handle(exchange, new RuntimeException("boom")))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }
}