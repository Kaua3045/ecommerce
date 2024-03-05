package com.kaua.ecommerce.infrastructure.customer;

import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerCacheGateway implements CacheGateway<Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerCacheGateway.class);

    private final CustomerCacheEntityRepository customerCacheEntityRepository;

    public CustomerCacheGateway(final CustomerCacheEntityRepository customerCacheEntityRepository) {
        this.customerCacheEntityRepository = Objects.requireNonNull(customerCacheEntityRepository);
    }

    @Override
    public void save(Customer aggregateRoot) {
        this.customerCacheEntityRepository.save(CustomerCacheEntity.toEntity(aggregateRoot));
        log.info("inserted customer in cache: {}", aggregateRoot);
    }

    @Override
    public Optional<Customer> get(String id) {
        return this.customerCacheEntityRepository.findById(id).map(CustomerCacheEntity::toDomain);
    }

    @Override
    public void delete(String id) {
        if (this.customerCacheEntityRepository.existsById(id)) {
            this.customerCacheEntityRepository.deleteById(id);
            log.info("deleted customer in cache with id: {}", id);
        }
    }
}
