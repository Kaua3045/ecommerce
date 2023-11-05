package com.kaua.ecommerce.infrastructure.customer.address.persistence;

import org.springframework.data.repository.CrudRepository;

public interface AddressCacheRepository extends CrudRepository<AddressCacheEntity, String> {
}
