package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.DefaultCreateInventoryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class InventoryUseCaseConfig {

    private final InventoryGateway inventoryGateway;

    public InventoryUseCaseConfig(final InventoryGateway inventoryGateway) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
    }

    @Bean
    public CreateInventoryUseCase createInventoryUseCase() {
        return new DefaultCreateInventoryUseCase(inventoryGateway);
    }
}
