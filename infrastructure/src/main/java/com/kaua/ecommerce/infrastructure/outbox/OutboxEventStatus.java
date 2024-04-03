package com.kaua.ecommerce.infrastructure.outbox;

public enum OutboxEventStatus {

    PENDING,
    FAILED,
    PROCESSED
}
