package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@Import({BranchTopProductAdapter.class, ProductAdapter.class,
        BranchAdapter.class, FranchiseAdapter.class})
class BranchTopProductAdapterIntegrationTest extends AbstractPostgresIntegrationTest {

    private static final String FRANCHISE_ID = "f-1";
    private static final String CENTRO_ID = "b-1";
    private static final String NORTE_ID = "b-2";
    private static final String SUR_ID = "b-3";

    @Autowired
    private BranchTopProductAdapter adapter;

    @Autowired
    private ProductAdapter productAdapter;

    @Autowired
    private BranchAdapter branchAdapter;

    @Autowired
    private FranchiseAdapter franchiseAdapter;

    private void seedFranchiseWithBranches() {
        franchiseAdapter.create(Franchise.builder()
                .id(FRANCHISE_ID).name("Mi Franquicia").build()).block();
        branchAdapter.create(Branch.builder()
                .id(CENTRO_ID).franchiseId(FRANCHISE_ID).name("Centro").build()).block();
        branchAdapter.create(Branch.builder()
                .id(NORTE_ID).franchiseId(FRANCHISE_ID).name("Norte").build()).block();
    }

    private void addProduct(String id, String branchId, String name, int stock) {
        productAdapter.create(Product.builder()
                .id(id).branchId(branchId).name(name).stock(stock).build()).block();
    }

    @Test
    void shouldReturnTheProductWithTheHighestStockPerBranch() {
        seedFranchiseWithBranches();
        addProduct("p-1", CENTRO_ID, "Café", 40);
        addProduct("p-2", CENTRO_ID, "Té", 10);
        addProduct("p-3", NORTE_ID, "Pan", 25);
        addProduct("p-4", NORTE_ID, "Leche", 5);

        StepVerifier.create(adapter.findTopProductsByFranchiseId(FRANCHISE_ID))
                .expectNextMatches(result -> "Centro".equals(result.getBranchName())
                        && "Café".equals(result.getProductName())
                        && result.getStock() == 40)
                .expectNextMatches(result -> "Norte".equals(result.getBranchName())
                        && "Pan".equals(result.getProductName())
                        && result.getStock() == 25)
                .verifyComplete();
    }

    @Test
    void shouldReturnAllProductsTiedForTheHighestStock() {
        seedFranchiseWithBranches();
        addProduct("p-1", NORTE_ID, "Pan", 25);
        addProduct("p-2", NORTE_ID, "Leche", 25);
        addProduct("p-3", NORTE_ID, "Té", 5);

        StepVerifier.create(adapter.findTopProductsByFranchiseId(FRANCHISE_ID))
                .expectNextMatches(result -> "Leche".equals(result.getProductName())
                        && result.getStock() == 25)
                .expectNextMatches(result -> "Pan".equals(result.getProductName())
                        && result.getStock() == 25)
                .verifyComplete();
    }

    @Test
    void shouldOmitBranchesWithoutProducts() {
        seedFranchiseWithBranches();
        branchAdapter.create(Branch.builder()
                .id(SUR_ID).franchiseId(FRANCHISE_ID).name("Sur").build()).block();
        addProduct("p-1", CENTRO_ID, "Café", 40);

        StepVerifier.create(adapter.findTopProductsByFranchiseId(FRANCHISE_ID))
                .expectNextMatches(result -> "Centro".equals(result.getBranchName()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenFranchiseHasNoBranches() {
        franchiseAdapter.create(Franchise.builder()
                .id(FRANCHISE_ID).name("Mi Franquicia").build()).block();

        StepVerifier.create(adapter.findTopProductsByFranchiseId(FRANCHISE_ID))
                .verifyComplete();
    }
}