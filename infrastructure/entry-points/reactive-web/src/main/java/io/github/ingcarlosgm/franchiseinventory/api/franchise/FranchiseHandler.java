package io.github.ingcarlosgm.franchiseinventory.api.franchise;

import io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise.CreateFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;

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
}