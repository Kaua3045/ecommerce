package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.customer.Customer;

import java.util.Optional;

public interface CustomerGateway {

    Customer create(Customer aCustomer);

    boolean existsByAccountId(String aAccountId);

    Optional<Customer> findByAccountId(String aAccountId);

    Customer update(Customer aCustomer);

    void deleteById(String aAccountId);
}
