package com.kaua.ecommerce.infrastructure.customer.address.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaEntityRepository extends JpaRepository<AddressJpaEntity, String> {
}
