package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.create.DefaultCreateCustomerUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CustomerUseCaseConfig {

    private final CustomerGateway customerGateway;

    public CustomerUseCaseConfig(final CustomerGateway customerGateway) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new DefaultCreateCustomerUseCase(customerGateway);
    }
}
