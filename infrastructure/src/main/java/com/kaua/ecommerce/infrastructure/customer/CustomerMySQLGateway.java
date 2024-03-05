package com.kaua.ecommerce.infrastructure.customer;

import com.kaua.ecommerce.application.gateways.CustomerGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerMySQLGateway implements CustomerGateway {

    private static final Logger log = LoggerFactory.getLogger(CustomerMySQLGateway.class);

    private final CustomerJpaEntityRepository customerEntityRepository;

    public CustomerMySQLGateway(final CustomerJpaEntityRepository customerEntityRepository) {
        this.customerEntityRepository = Objects.requireNonNull(customerEntityRepository);
    }

    @Override
    public Customer create(Customer aCustomer) {
        final var aResult = this.customerEntityRepository.save(CustomerJpaEntity.toEntity(aCustomer)).toDomain();
        log.info("inserted customer: {}", aResult);
        return aResult;
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return this.customerEntityRepository.existsByAccountId(accountId);
    }

    @Override
    public Optional<Customer> findByAccountId(String aAccountId) {
        return this.customerEntityRepository.findByAccountId(aAccountId).map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Customer update(Customer aCustomer) {
        final var aResult = this.customerEntityRepository.save(CustomerJpaEntity.toEntity(aCustomer)).toDomain();
        log.info("updated customer: {}", aResult);
        return aResult;
    }

    @Transactional
    @Override
    public void deleteById(String aAccountId) {
        if (this.customerEntityRepository.existsByAccountId(aAccountId)) {
            this.customerEntityRepository.deleteByAccountId(aAccountId);
            log.info("deleted customer with accountId: {}", aAccountId);
        }
    }
}
