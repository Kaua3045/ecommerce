package com.kaua.ecommerce.infrastructure.customer.address.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaRepository extends JpaRepository<AddressJpaEntity, String> {
}
