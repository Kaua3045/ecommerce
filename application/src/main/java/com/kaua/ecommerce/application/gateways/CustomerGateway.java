package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.customer.Customer;

public interface CustomerGateway {

    Customer create(Customer aCustomer);

    boolean existsByAccountId(String accountId);
}
