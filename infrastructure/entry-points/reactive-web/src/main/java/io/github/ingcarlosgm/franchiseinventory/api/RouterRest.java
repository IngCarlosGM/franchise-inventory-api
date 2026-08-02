package io.github.ingcarlosgm.franchiseinventory.api;

import io.github.ingcarlosgm.franchiseinventory.api.branch.BranchHandler;
import io.github.ingcarlosgm.franchiseinventory.api.franchise.FranchiseHandler;
import io.github.ingcarlosgm.franchiseinventory.api.product.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler franchiseHandler,
                                                         BranchHandler branchHandler,
                                                         ProductHandler productHandler) {
        return route(POST("/franchises"), franchiseHandler::createFranchise)
                .andRoute(POST("/franchises/{franchiseId}/branches"), branchHandler::addBranch)
                .andRoute(POST("/branches/{branchId}/products"), productHandler::addProduct)
                .andRoute(DELETE("/products/{productId}"), productHandler::removeProduct);
    }
}