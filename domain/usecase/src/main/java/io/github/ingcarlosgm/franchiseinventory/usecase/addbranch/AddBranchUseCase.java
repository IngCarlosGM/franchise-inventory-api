package io.github.ingcarlosgm.franchiseinventory.usecase.addbranch;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.branch.gateways.BranchRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final IdentityGenerator identityGenerator;

    public Mono<Branch> addBranch(Branch branch) {
        Mono<Boolean> franchiseExists = franchiseRepository.findById(branch.getFranchiseId())
                .map(franchise -> true)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("la franquicia", branch.getFranchiseId())));

        Mono<Boolean> nameInUse = branchRepository
                .existsByFranchiseIdAndName(branch.getFranchiseId(), branch.getName());

        return Mono.zip(franchiseExists, nameInUse)
                .flatMap(result -> Boolean.TRUE.equals(result.getT2())
                        ? Mono.<Branch>error(new DuplicateNameException(
                        branch.getName(), "la franquicia " + branch.getFranchiseId()))
                        : branchRepository.create(
                        branch.toBuilder()
                                .id(identityGenerator.generate())
                                .build()));
    }
}