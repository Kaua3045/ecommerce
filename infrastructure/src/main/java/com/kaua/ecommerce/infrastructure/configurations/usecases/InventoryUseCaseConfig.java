package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.create.DefaultCreateInventoryUseCase;
import com.kaua.ecommerce.application.usecases.inventory.decrease.DecreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.decrease.DefaultDecreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.CleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.clean.DefaultCleanInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.DefaultRemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.delete.remove.RemoveInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.increase.DefaultIncreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.increase.IncreaseInventoryQuantityUseCase;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.get.DefaultGetInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.get.GetInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.list.DefaultListInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.list.ListInventoriesByProductIdUseCase;
import com.kaua.ecommerce.application.usecases.inventory.rollback.DefaultRollbackInventoryBySkuUseCase;
import com.kaua.ecommerce.application.usecases.inventory.rollback.RollbackInventoryBySkuUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class InventoryUseCaseConfig {

    private final InventoryGateway inventoryGateway;
    private final InventoryMovementGateway inventoryMovementGateway;
    private final TransactionManager transactionManager;

    public InventoryUseCaseConfig(
            final InventoryGateway inventoryGateway,
            final InventoryMovementGateway inventoryMovementGateway,
            final TransactionManager transactionManager
    ) {
        this.inventoryGateway = Objects.requireNonNull(inventoryGateway);
        this.inventoryMovementGateway = Objects.requireNonNull(inventoryMovementGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Bean
    public CreateInventoryUseCase createInventoryUseCase() {
        return new DefaultCreateInventoryUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public CleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase() {
        return new DefaultCleanInventoriesByProductIdUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public IncreaseInventoryQuantityUseCase increaseInventoryQuantityUseCase() {
        return new DefaultIncreaseInventoryQuantityUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public DecreaseInventoryQuantityUseCase decreaseInventoryQuantityUseCase() {
        return new DefaultDecreaseInventoryQuantityUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public RemoveInventoryBySkuUseCase removeInventoryBySkuUseCase() {
        return new DefaultRemoveInventoryBySkuUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public RollbackInventoryBySkuUseCase rollbackBySkuUseCase() {
        return new DefaultRollbackInventoryBySkuUseCase(inventoryGateway, inventoryMovementGateway, transactionManager);
    }

    @Bean
    public ListInventoriesByProductIdUseCase listInventoriesByProductIdUseCase() {
        return new DefaultListInventoriesByProductIdUseCase(inventoryGateway);
    }

    @Bean
    public GetInventoryBySkuUseCase getInventoryBySkuUseCase() {
        return new DefaultGetInventoryBySkuUseCase(inventoryGateway);
    }
}
