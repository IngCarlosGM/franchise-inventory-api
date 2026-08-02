package io.github.ingcarlosgm.franchiseinventory.api.product;

import io.github.ingcarlosgm.franchiseinventory.usecase.addproduct.AddProductUseCase;
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
}