package com.kaua.ecommerce.infrastructure.customer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerjpaRepository extends JpaRepository<CustomerJpaEntity, String> {

    boolean existsByAccountId(String accountId);

    Optional<CustomerJpaEntity> findByAccountId(String accountId);
}
