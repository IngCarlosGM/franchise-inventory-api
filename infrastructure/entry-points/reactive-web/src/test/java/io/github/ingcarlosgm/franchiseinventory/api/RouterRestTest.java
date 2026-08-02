package io.github.ingcarlosgm.franchiseinventory.api;

import io.github.ingcarlosgm.franchiseinventory.api.branch.BranchHandler;
import io.github.ingcarlosgm.franchiseinventory.api.error.GlobalErrorHandler;
import io.github.ingcarlosgm.franchiseinventory.api.franchise.FranchiseHandler;
import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.usecase.addbranch.AddBranchUseCase;
import io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise.CreateFranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class, BranchHandler.class})
@WebFluxTest
@Import(GlobalErrorHandler.class)
class RouterRestTest {

    private static final String FRANCHISE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String BRANCH_ID = "22222222-2222-2222-2222-222222222222";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockitoBean
    private AddBranchUseCase addBranchUseCase;

    @Test
    void shouldCreateFranchise() {
        Franchise created = Franchise.builder()
                .id(FRANCHISE_ID)
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
                .jsonPath("$.id").isEqualTo(FRANCHISE_ID)
                .jsonPath("$.name").isEqualTo("Mi Franquicia");
    }

    @Test
    void shouldReturnConflictWhenFranchiseNameIsAlreadyInUse() {
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
    void shouldReturnBadRequestWhenFranchiseNameIsEmpty() {
        webTestClient.post()
                .uri("/franchises")
                .bodyValue(Map.of("name", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("INVALID_DATA");
    }

    @Test
    void shouldAddBranch() {
        Branch created = Branch.builder()
                .id(BRANCH_ID)
                .franchiseId(FRANCHISE_ID)
                .name("Centro")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(addBranchUseCase.addBranch(any(Branch.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/franchises/{franchiseId}/branches", FRANCHISE_ID)
                .bodyValue(Map.of("name", "Centro"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(BRANCH_ID)
                .jsonPath("$.franchiseId").isEqualTo(FRANCHISE_ID)
                .jsonPath("$.name").isEqualTo("Centro");
    }

    @Test
    void shouldReturnNotFoundWhenFranchiseDoesNotExist() {
        when(addBranchUseCase.addBranch(any(Branch.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("la franquicia", FRANCHISE_ID)));

        webTestClient.post()
                .uri("/franchises/{franchiseId}/branches", FRANCHISE_ID)
                .bodyValue(Map.of("name", "Centro"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("RESOURCE_NOT_FOUND");
    }
}