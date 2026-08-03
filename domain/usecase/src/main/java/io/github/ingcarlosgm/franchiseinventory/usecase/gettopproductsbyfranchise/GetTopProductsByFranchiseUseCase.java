package io.github.ingcarlosgm.franchiseinventory.usecase.gettopproductsbyfranchise;

import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.BranchTopProduct;
import io.github.ingcarlosgm.franchiseinventory.model.branchtopproduct.gateways.BranchTopProductRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTopProductsByFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchTopProductRepository branchTopProductRepository;

    public Flux<BranchTopProduct> getTopProductsByFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("la franquicia", franchiseId)))
                .flatMapMany(franchise ->
                        branchTopProductRepository.findTopProductsByFranchiseId(franchiseId));
    }
}