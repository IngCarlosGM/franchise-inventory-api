package io.github.ingcarlosgm.franchiseinventory.api.product;

import io.github.ingcarlosgm.franchiseinventory.usecase.addproduct.AddProductUseCase;
import io.github.ingcarlosgm.franchiseinventory.usecase.removeproduct.RemoveProductUseCase;
import io.github.ingcarlosgm.franchiseinventory.usecase.updateproductstock.UpdateProductStockUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final AddProductUseCase addProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");

        return request.bodyToMono(CreateProductRequest.class)
                .map(body -> ProductApiMapper.toDomain(body, branchId))
                .flatMap(addProductUseCase::addProduct)
                .doOnSuccess(product ->
                        log.info("Producto creado con id {} en la sucursal {}",
                                product.getId(), branchId))
                .doOnError(error ->
                        log.warn("Fallo al crear el producto en la sucursal {}: {}",
                                branchId, error.getMessage()))
                .map(ProductApiMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String productId = request.pathVariable("productId");

        return removeProductUseCase.removeProduct(productId)
                .doOnSuccess(unused -> log.info("Producto eliminado con id {}", productId))
                .doOnError(error ->
                        log.warn("Fallo al eliminar el producto {}: {}",
                                productId, error.getMessage()))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String productId = request.pathVariable("productId");

        return request.bodyToMono(UpdateProductRequest.class)
                .flatMap(body -> updateProductStockUseCase
                        .updateProductStock(productId, body.getStock()))
                .doOnSuccess(product ->
                        log.info("Stock actualizado a {} para el producto {}",
                                product.getStock(), productId))
                .doOnError(error ->
                        log.warn("Fallo al actualizar el stock del producto {}: {}",
                                productId, error.getMessage()))
                .map(ProductApiMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}