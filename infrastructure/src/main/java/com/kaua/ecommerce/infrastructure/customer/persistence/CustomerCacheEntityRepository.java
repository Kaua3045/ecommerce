package com.kaua.ecommerce.infrastructure.customer.persistence;

import org.springframework.data.repository.CrudRepository;

public interface CustomerCacheEntityRepository extends CrudRepository<CustomerCacheEntity, String> {
}
