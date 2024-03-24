package com.kaua.ecommerce.application.gateways.order;

import java.util.Optional;

public interface OrderCustomerGateway {

    Optional<OrderCustomerOutput> findByCustomerId(String customerId);

    record OrderCustomerOutput(
            String customerId,
            String zipCode,
            String street,
            String number,
            String complement,
            String district,
            String city,
            String state
    ) {}
}
