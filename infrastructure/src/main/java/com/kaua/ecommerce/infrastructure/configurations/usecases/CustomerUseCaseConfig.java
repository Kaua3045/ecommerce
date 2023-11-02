package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.AddressAdapter;
import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.application.usecases.customer.create.CreateCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.create.DefaultCreateCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.delete.DefaultDeleteCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.delete.DeleteCustomerUseCase;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.DefaultGetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.address.DefaultUpdateCustomerAddressUseCase;
import com.kaua.ecommerce.application.usecases.customer.update.address.UpdateCustomerAddressUseCase;
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
    private final AddressAdapter addressAdapter;
    private final AddressGateway addressGateway;

    public CustomerUseCaseConfig(
            final CustomerGateway customerGateway,
            final TelephoneAdapter telephoneAdapter,
            final AddressAdapter addressAdapter,
            final AddressGateway addressGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.telephoneAdapter = Objects.requireNonNull(telephoneAdapter);
        this.addressAdapter = Objects.requireNonNull(addressAdapter);
        this.addressGateway = Objects.requireNonNull(addressGateway);
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

    @Bean
    public UpdateCustomerAddressUseCase updateCustomerAddressUseCase() {
        return new DefaultUpdateCustomerAddressUseCase(customerGateway, addressGateway, addressAdapter);
    }

    @Bean
    public GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase() {
        return new DefaultGetCustomerByAccountIdUseCase(customerGateway);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase() {
        return new DefaultDeleteCustomerUseCase(customerGateway);
    }
}
