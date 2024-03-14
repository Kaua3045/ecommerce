package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.usecases.freight.list.DefaultListFreightsByCepUseCase;
import com.kaua.ecommerce.application.usecases.freight.list.ListFreightsByCepUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class FreightUseCaseConfig {

    private final FreightGateway freightGateway;

    public FreightUseCaseConfig(final FreightGateway freightGateway) {
        this.freightGateway = Objects.requireNonNull(freightGateway);
    }

    @Bean
    public ListFreightsByCepUseCase listFreightsByCepUseCase() {
        return new DefaultListFreightsByCepUseCase(this.freightGateway);
    }
}
