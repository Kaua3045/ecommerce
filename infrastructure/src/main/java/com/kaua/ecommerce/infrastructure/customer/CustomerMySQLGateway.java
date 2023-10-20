package com.kaua.ecommerce.infrastructure.customer;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerjpaRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerMySQLGateway implements CustomerGateway {

    private final CustomerjpaRepository customerRepository;

    public CustomerMySQLGateway(final CustomerjpaRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Override
    public Customer create(Customer aCustomer) {
        return this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer)).toDomain();
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return this.customerRepository.existsByAccountId(accountId);
    }
}
