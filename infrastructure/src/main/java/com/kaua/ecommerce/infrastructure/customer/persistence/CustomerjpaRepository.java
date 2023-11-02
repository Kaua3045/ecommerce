package com.kaua.ecommerce.infrastructure.customer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerjpaRepository extends JpaRepository<CustomerJpaEntity, String> {

    boolean existsByAccountId(String accountId);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.address WHERE c.accountId = :accountId")
    Optional<CustomerJpaEntity> findByAccountId(String accountId);

    void deleteByAccountId(String accountId);
}
