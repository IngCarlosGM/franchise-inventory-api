package io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise;

import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final IdentityGenerator identityGenerator;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.existsByName(franchise.getName())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.<Franchise>error(
                        new DuplicateNameException(franchise.getName(), "las franquicias"))
                        : franchiseRepository.create(
                        franchise.toBuilder()
                                .id(identityGenerator.generate())
                                .build()));
    }
}