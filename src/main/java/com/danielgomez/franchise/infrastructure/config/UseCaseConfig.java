package com.danielgomez.franchise.infrastructure.config;

import com.danielgomez.franchise.domain.port.out.FranchiseRepository;
import com.danielgomez.franchise.usecase.franchise.FranchiseUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Crea los casos de uso como beans, así no llevan anotaciones de Spring.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public FranchiseUseCase franchiseUseCase(FranchiseRepository franchiseRepository) {
        return new FranchiseUseCase(franchiseRepository);
    }
}
