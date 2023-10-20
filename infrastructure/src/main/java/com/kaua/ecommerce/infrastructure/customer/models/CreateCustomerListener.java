package com.kaua.ecommerce.infrastructure.customer.models;

import java.time.Instant;

public record CreateCustomerListener(
        String id,
        String firstName,
        String lastName,
        String email,
        Instant occurredOn
) {
}
