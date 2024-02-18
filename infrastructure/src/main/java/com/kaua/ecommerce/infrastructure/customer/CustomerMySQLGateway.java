package com.kaua.ecommerce.infrastructure.customer;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerMySQLGateway implements CustomerGateway {

    private static final Logger log = LoggerFactory.getLogger(CustomerMySQLGateway.class);

    private final CustomerJpaRepository customerRepository;

    public CustomerMySQLGateway(final CustomerJpaRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Override
    public Customer create(Customer aCustomer) {
        final var aResult = this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer)).toDomain();
        log.info("inserted customer: {}", aResult);
        return aResult;
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return this.customerRepository.existsByAccountId(accountId);
    }

    @Override
    public Optional<Customer> findByAccountId(String aAccountId) {
        return this.customerRepository.findByAccountId(aAccountId).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Customer update(Customer aCustomer) {
        final var aResult = this.customerRepository.save(CustomerJpaEntity.toEntity(aCustomer)).toDomain();
        log.info("updated customer: {}", aResult);
        return aResult;
    }

    @Transactional
    @Override
    public void deleteById(String aAccountId) {
        if (this.customerRepository.existsByAccountId(aAccountId)){
            this.customerRepository.deleteByAccountId(aAccountId);
            log.info("deleted customer with accountId: {}", aAccountId);
        }
    }
}
