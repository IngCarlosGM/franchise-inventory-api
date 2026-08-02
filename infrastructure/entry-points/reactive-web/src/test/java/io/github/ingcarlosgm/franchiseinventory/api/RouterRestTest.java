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
import io.github.ingcarlosgm.franchiseinventory.api.product.ProductHandler;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.usecase.addproduct.AddProductUseCase;
import io.github.ingcarlosgm.franchiseinventory.usecase.removeproduct.RemoveProductUseCase;
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

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class, BranchHandler.class, ProductHandler.class})
@WebFluxTest
@Import(GlobalErrorHandler.class)
class RouterRestTest {

    private static final String FRANCHISE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String BRANCH_ID = "22222222-2222-2222-2222-222222222222";
    private static final String PRODUCT_ID = "33333333-3333-3333-3333-333333333333";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockitoBean
    private AddBranchUseCase addBranchUseCase;

    @MockitoBean
    private AddProductUseCase addProductUseCase;

    @MockitoBean
    private RemoveProductUseCase removeProductUseCase;

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

    @Test
    void shouldAddProduct() {
        Product created = Product.builder()
                .id(PRODUCT_ID)
                .branchId(BRANCH_ID)
                .name("Café")
                .stock(40)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(addProductUseCase.addProduct(any(Product.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/branches/{branchId}/products", BRANCH_ID)
                .bodyValue(Map.of("name", "Café", "stock", 40))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(PRODUCT_ID)
                .jsonPath("$.branchId").isEqualTo(BRANCH_ID)
                .jsonPath("$.stock").isEqualTo(40);
    }

    @Test
    void shouldReturnBadRequestWhenStockIsNegative() {
        webTestClient.post()
                .uri("/branches/{branchId}/products", BRANCH_ID)
                .bodyValue(Map.of("name", "Café", "stock", -1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("INVALID_DATA")
                .jsonPath("$.errors[0].field").isEqualTo("stock");
    }

    @Test
    void shouldRemoveProduct() {
        when(removeProductUseCase.removeProduct(PRODUCT_ID)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/products/{productId}", PRODUCT_ID)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenRemovingAProductThatDoesNotExist() {
        when(removeProductUseCase.removeProduct(PRODUCT_ID))
                .thenReturn(Mono.error(new ResourceNotFoundException("el producto", PRODUCT_ID)));

        webTestClient.delete()
                .uri("/products/{productId}", PRODUCT_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("RESOURCE_NOT_FOUND");
    }
}