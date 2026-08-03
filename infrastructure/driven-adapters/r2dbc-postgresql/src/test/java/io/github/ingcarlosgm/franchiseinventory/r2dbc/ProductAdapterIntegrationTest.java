package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import({ProductAdapter.class, BranchAdapter.class, FranchiseAdapter.class})
class ProductAdapterIntegrationTest extends AbstractPostgresIntegrationTest {

    private static final String FRANCHISE_ID = "f-1";
    private static final String BRANCH_ID = "b-1";
    private static final String OTHER_BRANCH_ID = "b-2";

    @Autowired
    private ProductAdapter productAdapter;

    @Autowired
    private BranchAdapter branchAdapter;

    @Autowired
    private FranchiseAdapter franchiseAdapter;

    private void seedParents() {
        franchiseAdapter.create(Franchise.builder()
                .id(FRANCHISE_ID).name("Mi Franquicia").build()).block();
        branchAdapter.create(Branch.builder()
                .id(BRANCH_ID).franchiseId(FRANCHISE_ID).name("Centro").build()).block();
        branchAdapter.create(Branch.builder()
                .id(OTHER_BRANCH_ID).franchiseId(FRANCHISE_ID).name("Norte").build()).block();
    }

    @Test
    void shouldInsertProductAndFillTimestamps() {
        seedParents();

        Product created = productAdapter.create(Product.builder()
                .id("p-1").branchId(BRANCH_ID).name("Café").stock(40).build()).block();

        assertNotNull(created);
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());

        StepVerifier.create(productAdapter.findById("p-1"))
                .expectNextMatches(product -> "Café".equals(product.getName())
                        && product.getStock() == 40)
                .verifyComplete();
    }

    @Test
    void shouldUpdateStockKeepingCreatedAt() throws InterruptedException {
        seedParents();

        Product created = productAdapter.create(Product.builder()
                .id("p-1").branchId(BRANCH_ID).name("Café").stock(40).build()).block();

        assertNotNull(created);
        Instant originalCreatedAt = created.getCreatedAt();

        Thread.sleep(10);

        Product updated = productAdapter.update(created.toBuilder().stock(42).build()).block();

        assertNotNull(updated);
        assertEquals(42, updated.getStock());
        assertEquals(originalCreatedAt, updated.getCreatedAt());

        StepVerifier.create(productAdapter.findById("p-1"))
                .expectNextMatches(product -> product.getStock() == 42
                        && originalCreatedAt.equals(product.getCreatedAt()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        seedParents();
        productAdapter.create(Product.builder()
                .id("p-1").branchId(BRANCH_ID).name("Café").stock(40).build()).block();

        productAdapter.deleteById("p-1").block();

        StepVerifier.create(productAdapter.findById("p-1"))
                .verifyComplete();
    }

    @Test
    void shouldScopeNameUniquenessToTheBranch() {
        seedParents();
        productAdapter.create(Product.builder()
                .id("p-1").branchId(BRANCH_ID).name("Café").stock(40).build()).block();

        StepVerifier.create(productAdapter.existsByBranchIdAndName(BRANCH_ID, "Café"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(productAdapter.existsByBranchIdAndName(OTHER_BRANCH_ID, "Café"))
                .expectNext(false)
                .verifyComplete();
    }
}