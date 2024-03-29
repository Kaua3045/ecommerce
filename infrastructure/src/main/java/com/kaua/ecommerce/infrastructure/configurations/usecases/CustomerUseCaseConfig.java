package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TelephoneAdapter;
import com.kaua.ecommerce.application.gateways.AddressDatabaseGateway;
import com.kaua.ecommerce.application.gateways.AddressGateway;
import com.kaua.ecommerce.application.gateways.CacheGateway;
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
import com.kaua.ecommerce.domain.customer.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CustomerUseCaseConfig {

    private final CustomerGateway customerGateway;
    private final TelephoneAdapter telephoneAdapter;
    private final AddressGateway addressGateway;
    private final AddressDatabaseGateway addressDatabaseGateway;
    private final CacheGateway<Customer> customerCacheGateway;

    public CustomerUseCaseConfig(
            final CustomerGateway customerGateway,
            final TelephoneAdapter telephoneAdapter,
            final AddressGateway addressGateway,
            final AddressDatabaseGateway addressDatabaseGateway,
            final CacheGateway<Customer> customerCacheGateway
    ) {
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.telephoneAdapter = Objects.requireNonNull(telephoneAdapter);
        this.addressGateway = Objects.requireNonNull(addressGateway);
        this.addressDatabaseGateway = Objects.requireNonNull(addressDatabaseGateway);
        this.customerCacheGateway = Objects.requireNonNull(customerCacheGateway);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new DefaultCreateCustomerUseCase(customerGateway);
    }

    @Bean
    public UpdateCustomerCpfUseCase updateCustomerCpfUseCase() {
        return new DefaultUpdateCustomerCpfUseCase(customerGateway, customerCacheGateway);
    }

    @Bean
    public UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase() {
        return new DefaultUpdateCustomerTelephoneUseCase(customerGateway, telephoneAdapter, customerCacheGateway);
    }

    @Bean
    public UpdateCustomerAddressUseCase updateCustomerAddressUseCase() {
        return new DefaultUpdateCustomerAddressUseCase(customerGateway, addressGateway, addressDatabaseGateway, customerCacheGateway);
    }

    @Bean
    public GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase() {
        return new DefaultGetCustomerByAccountIdUseCase(customerGateway, customerCacheGateway);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase() {
        return new DefaultDeleteCustomerUseCase(customerGateway, customerCacheGateway);
    }
}
