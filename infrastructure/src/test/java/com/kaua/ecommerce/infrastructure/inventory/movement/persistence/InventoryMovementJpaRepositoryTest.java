package com.kaua.ecommerce.infrastructure.inventory.movement.persistence;

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
public class InventoryMovementJpaRepositoryTest {

    @Autowired
    private InventoryMovementJpaEntityRepository inventoryMovementRepository;

    @Test
    void givenAnInvalidNullInventoryId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "inventoryId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity.inventoryId";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setInventoryId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullSku_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "sku";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity.sku";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setSku(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullMovementType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "movementType";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity.movementType";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setMovementType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity.createdAt";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity.updatedAt";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntity";

        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> inventoryMovementRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidQuantity_whenCallSave_shouldReturnAnInventoryMovement() {
        final var aInventoryMovement = Fixture.InventoriesMovements.in();

        final var aEntity = InventoryMovementJpaEntity.toEntity(aInventoryMovement);
        aEntity.setQuantity(1);

        final var actualResult = Assertions.assertDoesNotThrow(() -> inventoryMovementRepository.save(aEntity))
                .toDomain();

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getInventoryId(), actualResult.getInventoryId().getValue());
        Assertions.assertEquals(aEntity.getSku(), actualResult.getSku());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
        Assertions.assertEquals(aEntity.getMovementType(), actualResult.getStatus());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }
}
