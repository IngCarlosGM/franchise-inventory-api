package io.github.ingcarlosgm.franchiseinventory.config;

import io.github.ingcarlosgm.franchiseinventory.model.franchise.gateways.FranchiseRepository;
import io.github.ingcarlosgm.franchiseinventory.model.gateways.IdentityGenerator;
import io.github.ingcarlosgm.franchiseinventory.usecase.createfranchise.CreateFranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void shouldRegisterUseCasesAsBeans() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            assertNotNull(context.getBean(CreateFranchiseUseCase.class));
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public FranchiseRepository franchiseRepository() {
            return mock(FranchiseRepository.class);
        }

        @Bean
        public IdentityGenerator identityGenerator() {
            return mock(IdentityGenerator.class);
        }
    }
}