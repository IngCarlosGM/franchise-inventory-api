package io.github.ingcarlosgm.franchiseinventory.usecase.addbranch;

import io.github.ingcarlosgm.franchiseinventory.model.branch.Branch;
import io.github.ingcarlosgm.franchiseinventory.model.branch.gateways.BranchRepository;
import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
import io.github.ingcarlosgm.franchiseinventory.model.exception.ResourceNotFoundException;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.Franchise;
import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddBranchUseCaseTest {

    private static final String FRANCHISE_ID = "22222222-2222-2222-2222-222222222222";
    private static final String OTHER_FRANCHISE_ID = "33333333-3333-3333-3333-333333333333";
    private static final String GENERATED_ID = "44444444-4444-4444-4444-444444444444";

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private IdentityGenerator identityGenerator;

    @InjectMocks
    private AddBranchUseCase useCase;

    private Branch branchNamed(String name, String franchiseId) {
        return Branch.builder().name(name).franchiseId(franchiseId).build();
    }

    @Test
    void shouldAddBranchWhenFranchiseExistsAndNameIsAvailable() {
        Branch input = branchNamed("Centro", FRANCHISE_ID);
        Branch persisted = input.toBuilder().id(GENERATED_ID).build();

        when(franchiseRepository.findById(FRANCHISE_ID))
                .thenReturn(Mono.just(Franchise.builder().id(FRANCHISE_ID).name("Mi Franquicia").build()));
        when(branchRepository.existsByFranchiseIdAndName(FRANCHISE_ID, "Centro"))
                .thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(branchRepository.create(any(Branch.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addBranch(input))
                .expectNextMatches(branch -> GENERATED_ID.equals(branch.getId())
                        && FRANCHISE_ID.equals(branch.getFranchiseId()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenFranchiseDoesNotExist() {
        Branch input = branchNamed("Centro", FRANCHISE_ID);

        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.empty());
        when(branchRepository.existsByFranchiseIdAndName(anyString(), anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(useCase.addBranch(input))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(branchRepository, never()).create(any(Branch.class));
    }

    @Test
    void shouldFailWhenNameIsAlreadyUsedInTheSameFranchise() {
        Branch input = branchNamed("Centro", FRANCHISE_ID);

        when(franchiseRepository.findById(FRANCHISE_ID))
                .thenReturn(Mono.just(Franchise.builder().id(FRANCHISE_ID).name("Mi Franquicia").build()));
        when(branchRepository.existsByFranchiseIdAndName(FRANCHISE_ID, "Centro"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.addBranch(input))
                .expectError(DuplicateNameException.class)
                .verify();

        verify(branchRepository, never()).create(any(Branch.class));
    }

    @Test
    void shouldAllowSameNameInAnotherFranchise() {
        Branch input = branchNamed("Centro", OTHER_FRANCHISE_ID);
        Branch persisted = input.toBuilder().id(GENERATED_ID).build();

        when(franchiseRepository.findById(OTHER_FRANCHISE_ID))
                .thenReturn(Mono.just(Franchise.builder().id(OTHER_FRANCHISE_ID).name("Otra Franquicia").build()));
        when(branchRepository.existsByFranchiseIdAndName(OTHER_FRANCHISE_ID, "Centro"))
                .thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(branchRepository.create(any(Branch.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.addBranch(input))
                .expectNextMatches(branch -> OTHER_FRANCHISE_ID.equals(branch.getFranchiseId()))
                .verifyComplete();
    }
}