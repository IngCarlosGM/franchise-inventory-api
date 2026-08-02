package io.github.ingcarlosgm.franchiseinventory.api.branch;

import io.github.ingcarlosgm.franchiseinventory.usecase.addbranch.AddBranchUseCase;
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
public class BranchHandler {

    private final AddBranchUseCase addBranchUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return request.bodyToMono(CreateBranchRequest.class)
                .map(body -> BranchApiMapper.toDomain(body, franchiseId))
                .flatMap(addBranchUseCase::addBranch)
                .doOnSuccess(branch ->
                        log.info("Sucursal creada con id {} en la franquicia {}",
                                branch.getId(), franchiseId))
                .doOnError(error ->
                        log.warn("Fallo al crear la sucursal en la franquicia {}: {}",
                                franchiseId, error.getMessage()))
                .map(BranchApiMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}