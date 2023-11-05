package com.kaua.ecommerce.infrastructure.customer;

import com.kaua.ecommerce.application.gateways.CacheGateway;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntity;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerCacheGateway implements CacheGateway<Customer> {

    private final CustomerCacheRepository customerCacheRepository;

    public CustomerCacheGateway(final CustomerCacheRepository customerCacheRepository) {
        this.customerCacheRepository = Objects.requireNonNull(customerCacheRepository);
    }

    @Override
    public void save(Customer aggregateRoot) {
        this.customerCacheRepository.save(CustomerCacheEntity.toEntity(aggregateRoot));
    }

    @Override
    public Optional<Customer> get(String id) {
        return this.customerCacheRepository.findById(id).map(CustomerCacheEntity::toDomain);
    }

    @Override
    public void delete(String id) {
        if (this.customerCacheRepository.existsById(id)) {
            this.customerCacheRepository.deleteById(id);
        }
    }
}
