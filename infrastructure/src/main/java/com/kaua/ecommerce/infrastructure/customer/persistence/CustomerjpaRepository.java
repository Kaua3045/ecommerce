package com.kaua.ecommerce.infrastructure.customer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerjpaRepository extends JpaRepository<CustomerJpaEntity, String> {

    boolean existsByAccountId(String accountId);
}
