package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.create.DefaultCreateCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.DefaultUpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.cpf.UpdateCustomerCpfUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.DefaultUpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.telephone.UpdateCustomerTelephoneUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CustomerUseCaseConfig {

    private final CustomerGateway customerGateway;
    private final TelephoneAdapter telephoneAdapter;

    public CustomerUseCaseConfig(final CustomerGateway customerGateway, final TelephoneAdapter telephoneAdapter) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.telephoneAdapter = Objects.requireNonNull(telephoneAdapter);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new DefaultCreateCustomerUseCase(customerGateway);
    }

    @Bean
    public UpdateCustomerCpfUseCase updateCustomerCpfUseCase() {
        return new DefaultUpdateCustomerCpfUseCase(customerGateway);
    }

    @Bean
    public UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase() {
        return new DefaultUpdateCustomerTelephoneUseCase(customerGateway, telephoneAdapter);
    }
}
