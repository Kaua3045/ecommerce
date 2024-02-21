package com.kaua.ecommerce.infrastructure.inventory.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@DatabaseGatewayTest
public class InventoryJpaRepositoryTest {

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAnInvalidNullProductId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "productId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity.productId";

        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setProductId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullSku_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "sku";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity.sku";

        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setSku(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity.createdAt";

        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity.updatedAt";

        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntity";

        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> inventoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidQuantity_whenCallSave_shouldReturnAnInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aEntity = InventoryJpaEntity.toEntity(aInventory);
        aEntity.setQuantity(1);

        final var actualResult = Assertions.assertDoesNotThrow(() -> inventoryRepository.save(aEntity));

        Assertions.assertEquals(aEntity.getId(), actualResult.getId());
        Assertions.assertEquals(aEntity.getProductId(), actualResult.getProductId());
        Assertions.assertEquals(aEntity.getSku(), actualResult.getSku());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }
}
