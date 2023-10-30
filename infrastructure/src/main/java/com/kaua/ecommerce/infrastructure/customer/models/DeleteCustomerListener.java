package com.kaua.ecommerce.infrastructure.customer.models;

import java.time.Instant;

public record DeleteCustomerListener(
        String id,
        Instant occurredOn
) {
}
