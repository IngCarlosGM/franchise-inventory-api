package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise.CreateFranchiseUseCase;
import io.github.ingcarlosgm.franchiseinventory.usecase.gettopproductsbyfranchise.GetTopProductsByFranchiseUseCase;
import io.github.ingcarlosgm.franchiseinventory.api.franchise.BranchTopProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetTopProductsByFranchiseUseCase getTopProductsByFranchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .map(FranchiseApiMapper::toDomain)
                .flatMap(createFranchiseUseCase::createFranchise)
                .doOnSuccess(franchise ->
                        log.info("Franquicia creada con id {}", franchise.getId()))
                .doOnError(error ->
                        log.warn("Fallo al crear la franquicia: {}", error.getMessage()))
                .map(FranchiseApiMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getTopProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return getTopProductsByFranchiseUseCase.getTopProductsByFranchise(franchiseId)
                .map(FranchiseApiMapper::toResponse)
                .collectList()
                .doOnSuccess(results ->
                        log.info("Consultados {} productos destacados de la franquicia {}",
                                results.size(), franchiseId))
                .doOnError(error ->
                        log.warn("Fallo al consultar los destacados de la franquicia {}: {}",
                                franchiseId, error.getMessage()))
                .flatMap(results -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(results));
    }
}