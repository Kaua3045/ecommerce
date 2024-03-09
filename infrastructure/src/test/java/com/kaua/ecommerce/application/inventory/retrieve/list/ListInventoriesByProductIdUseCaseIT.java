package com.kaua.ecommerce.application.inventory.retrieve.list;

import com.kaua.ecommerce.application.usecases.inventory.retrieve.list.ListInventoriesByProductIdCommand;
import com.kaua.ecommerce.application.usecases.inventory.retrieve.list.ListInventoriesByProductIdUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ListInventoriesByProductIdUseCaseIT {

    @Autowired
    private InventoryJpaEntityRepository inventoryJpaEntityRepository;

    @Autowired
    private ListInventoriesByProductIdUseCase listInventoriesByProductIdUseCase;

    @Test
    void givenAValidProductIdAndSearchQuery_whenListInventoriesByProductId_thenShouldReturnAPaginationOfInventories() {
        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(Fixture.Inventories.tshirtInventory()));

        final var aProductId = "1";
        final var aSku = "xpto";
        final var aQuantity = 10;

        final var aInventory = Inventory.newInventory(
                aProductId,
                aSku,
                aQuantity
        );

        final var aInventoryTwo = Inventory.newInventory(
                aProductId,
                "abablabla",
                aQuantity
        );

        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventory));
        this.inventoryJpaEntityRepository.save(InventoryJpaEntity.toEntity(aInventoryTwo));

        final var aSearchQuery = new SearchQuery(
                0,
                10,
                "x",
                "sku",
                "ASC"
        );

        Assertions.assertEquals(3, this.inventoryJpaEntityRepository.count());

        final var aOutput = this.listInventoriesByProductIdUseCase.execute(
                ListInventoriesByProductIdCommand.with(aProductId, aSearchQuery)
        );

        Assertions.assertEquals(0, aOutput.currentPage());
        Assertions.assertEquals(1, aOutput.totalItems());
        Assertions.assertEquals(1, aOutput.totalPages());
        Assertions.assertEquals(1, aOutput.items().size());

        final var aOutputInventory = aOutput.items().get(0);

        Assertions.assertEquals(aSku, aOutputInventory.sku());
        Assertions.assertEquals(aQuantity, aOutputInventory.quantity());
    }
}
