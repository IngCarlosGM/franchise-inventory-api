package io.github.ingcarlosgm.franchiseinventory.usecase.addproduct;

import io.github.ingcarlosgm.franchiseinventory.model.branch.gateways.BranchRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import io.github.ingcarlosgm.franchiseinventory.model.product.Product;
import io.github.ingcarlosgm.franchiseinventory.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final IdentityGenerator identityGenerator;

    public Mono<Product> addProduct(Product product) {
        Mono<Boolean> branchExists = branchRepository.findById(product.getBranchId())
                .map(branch -> true)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("la sucursal", product.getBranchId())
                        )
                );

        Mono<Boolean> nameInUse = productRepository
                .existsByBranchIdAndName(product.getBranchId(), product.getName());

        return Mono.zip(branchExists, nameInUse)
                .flatMap(result -> Boolean.TRUE.equals(result.getT2())
                        ? Mono.<Product>error(new DuplicateNameException(
                        product.getName(), "la sucursal " + product.getBranchId()))
                        : productRepository.create(
                        product.toBuilder()
                                .id(identityGenerator.generate())
                                .build()));
    }
}