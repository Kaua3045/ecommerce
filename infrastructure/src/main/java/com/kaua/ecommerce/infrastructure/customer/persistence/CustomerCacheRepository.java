package com.kaua.ecommerce.infrastructure.customer.persistence;

import org.springframework.data.repository.CrudRepository;

public interface CustomerCacheRepository extends CrudRepository<CustomerCacheEntity, String> {
}
