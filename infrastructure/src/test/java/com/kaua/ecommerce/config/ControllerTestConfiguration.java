package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.idempotency.IdempotencyKeyGateway;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class ControllerTestConfiguration {

    @Bean
    public IdempotencyKeyGateway idempotencyKeyGateway() {
        return Mockito.mock(IdempotencyKeyGateway.class);
    }
}