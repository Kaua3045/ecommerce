package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@DatabaseGatewayTest
public class ProductColorJpaRepositoryTest {

    @Autowired
    private ProductColorJpaEntityRepository productColorRepository;

    @Test
    void givenAnInvalidNullColor_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "color";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity.color";

        final var aProductColor = ProductColor.with("Red");

        final var aEntity = ProductColorJpaEntity.toEntity(aProductColor);
        aEntity.setColor(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productColorRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntity";

        final var aProductColor = ProductColor.with("Red");

        final var aEntity = ProductColorJpaEntity.toEntity(aProductColor);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> productColorRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidProductColor_whenCallSave_shouldPersistProductColor() {
        final var aProductColor = ProductColor.with("Red");

        final var aEntity = ProductColorJpaEntity.toEntity(aProductColor);

        Assertions.assertEquals(0, this.productColorRepository.count());

        final var aPersistedProductColor = this.productColorRepository.save(aEntity).toDomain();

        Assertions.assertEquals(aProductColor.getId(), aPersistedProductColor.getId());
        Assertions.assertEquals(aProductColor.getColor(), aPersistedProductColor.getColor());
        Assertions.assertEquals(1, this.productColorRepository.count());
    }
}
