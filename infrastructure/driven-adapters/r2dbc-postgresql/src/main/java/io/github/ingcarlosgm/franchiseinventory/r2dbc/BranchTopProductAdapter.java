package io.github.ingcarlosgm.franchiseinventory.r2dbc;

import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.BranchTopProduct;
import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.gateways.BranchTopProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class BranchTopProductAdapter implements BranchTopProductRepository {

    private static final String TOP_PRODUCTS_QUERY = """
            SELECT branch_id, branch_name, product_id, product_name, stock
            FROM (
                SELECT b.id            AS branch_id,
                       b.name          AS branch_name,
                       p.id            AS product_id,
                       p.name          AS product_name,
                       p.stock         AS stock,
                       RANK() OVER (PARTITION BY p.branch_id ORDER BY p.stock DESC) AS rk
                FROM product p
                JOIN branch b ON b.id = p.branch_id
                WHERE b.franchise_id = :franchiseId
            ) ranked
            WHERE rk = 1
            ORDER BY branch_name, product_name
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Flux<BranchTopProduct> findTopProductsByFranchiseId(String franchiseId) {
        return databaseClient.sql(TOP_PRODUCTS_QUERY)
                .bind("franchiseId", franchiseId)
                .map((row, metadata) -> BranchTopProduct.builder()
                        .branchId(row.get("branch_id", String.class))
                        .branchName(row.get("branch_name", String.class))
                        .productId(row.get("product_id", String.class))
                        .productName(row.get("product_name", String.class))
                        .stock(row.get("stock", Integer.class))
                        .build())
                .all();
    }
}