package io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise;

import io.github.ingcarlosgm.franchiseinventory.model.exception.DuplicateNameException;
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
class CreateFranchiseUseCaseTest {

    private static final String GENERATED_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private IdentityGenerator identityGenerator;

    @InjectMocks
    private CreateFranchiseUseCase useCase;

    @Test
    void shouldCreateFranchiseWhenNameIsAvailable() {
        Franchise input = Franchise.builder().name("Mi Franquicia").build();
        Franchise persisted = input.toBuilder().id(GENERATED_ID).build();

        when(franchiseRepository.existsByName("Mi Franquicia")).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(franchiseRepository.create(any(Franchise.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.createFranchise(input))
                .expectNextMatches(franchise -> GENERATED_ID.equals(franchise.getId())
                        && "Mi Franquicia".equals(franchise.getName()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenNameIsAlreadyInUse() {
        Franchise input = Franchise.builder().name("Mi Franquicia").build();

        when(franchiseRepository.existsByName("Mi Franquicia")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createFranchise(input))
                .expectError(DuplicateNameException.class)
                .verify();

        verify(franchiseRepository, never()).create(any(Franchise.class));
    }

    @Test
    void shouldKeepOptionalAttributesWhenProvided() {
        Franchise input = Franchise.builder()
                .name("Mi Franquicia")
                .contactEmail("contacto@marca.com")
                .website("https://marca.com")
                .build();
        Franchise persisted = input.toBuilder().id(GENERATED_ID).build();

        when(franchiseRepository.existsByName(anyString())).thenReturn(Mono.just(false));
        when(identityGenerator.generate()).thenReturn(GENERATED_ID);
        when(franchiseRepository.create(any(Franchise.class))).thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.createFranchise(input))
                .expectNextMatches(franchise ->
                        "contacto@marca.com".equals(franchise.getContactEmail())
                                && "https://marca.com".equals(franchise.getWebsite()))
                .verifyComplete();
    }
}