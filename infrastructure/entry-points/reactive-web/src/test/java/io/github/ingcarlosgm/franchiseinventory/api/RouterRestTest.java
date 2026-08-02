package io.github.ingcarlosgm.franchiseinventory.api;

import io.github.ingcarlosgm.franchiseinventory.api.error.GlobalErrorHandler;
import io.github.ingcarlosgm.franchiseinventory.api.franchise.FranchiseHandler;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise.CreateFranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class})
@WebFluxTest
@Import(GlobalErrorHandler.class)
class RouterRestTest {

    private static final String ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Test
    void shouldCreateFranchise() {
        Franchise created = Franchise.builder()
                .id(ID)
                .name("Mi Franquicia")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/franchises")
                .bodyValue(Map.of("name", "Mi Franquicia"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo("Mi Franquicia");
    }

    @Test
    void shouldReturnConflictWhenNameIsAlreadyInUse() {
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new DuplicateNameException("Mi Franquicia", "las franquicias")));

        webTestClient.post()
                .uri("/franchises")
                .bodyValue(Map.of("name", "Mi Franquicia"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("DUPLICATE_NAME");
    }

    @Test
    void shouldReturnBadRequestWhenNameIsEmpty() {
        webTestClient.post()
                .uri("/franchises")
                .bodyValue(Map.of("name", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("INVALID_DATA");
    }
}