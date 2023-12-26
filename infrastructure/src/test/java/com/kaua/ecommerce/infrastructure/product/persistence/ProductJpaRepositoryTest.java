package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.Set;

@DatabaseGatewayTest
public class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    void givenAnInvalidNullName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity.name";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidPrice_whenCallSave_shouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setPrice(1);

        final var actualOutput = Assertions.assertDoesNotThrow(() -> productRepository.save(aEntity));

        Assertions.assertEquals(aEntity.getId(), actualOutput.getId());
        Assertions.assertEquals(aEntity.getName(), actualOutput.getName());
        Assertions.assertEquals(aEntity.getDescription(), actualOutput.getDescription());
        Assertions.assertEquals(aEntity.getPrice(), actualOutput.getPrice());
        Assertions.assertEquals(aEntity.getQuantity(), actualOutput.getQuantity());
        Assertions.assertEquals(aEntity.getCategoryId(), actualOutput.getCategoryId());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualOutput.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualOutput.getUpdatedAt());
    }

    @Test
    void givenAValidQuantity_whenCallSave_shouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setQuantity(1);

        final var actualOutput = Assertions.assertDoesNotThrow(() -> productRepository.save(aEntity));

        Assertions.assertEquals(aEntity.getId(), actualOutput.getId());
        Assertions.assertEquals(aEntity.getName(), actualOutput.getName());
        Assertions.assertEquals(aEntity.getDescription(), actualOutput.getDescription());
        Assertions.assertEquals(aEntity.getPrice(), actualOutput.getPrice());
        Assertions.assertEquals(aEntity.getQuantity(), actualOutput.getQuantity());
        Assertions.assertEquals(aEntity.getCategoryId(), actualOutput.getCategoryId());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualOutput.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualOutput.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullCategoryId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "categoryId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity.categoryId";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setCategoryId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity.createdAt";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity.updatedAt";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntity";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidNullDescription_whenCallSave_shouldReturnProduct() {
        final var aProduct = Product.newProduct(
                "Product Name",
                "Product Description",
                10.0,
                10,
                CategoryID.unique(),
                ProductAttributes.create(
                        ProductColor.with("1", "Red"),
                        ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                        "Product Name")
        );

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setDescription(null);

        final var actualResult = Assertions.assertDoesNotThrow(() -> productRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getName(), actualResult.getName());
        Assertions.assertNull(actualResult.getDescription());
        Assertions.assertEquals(aEntity.getPrice(), actualResult.getPrice());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
        Assertions.assertEquals(aEntity.getCategoryId(), actualResult.getCategoryId().getValue());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAValidEmptyAttributes_whenCallSave_shouldReturnProduct() {
        final var aProduct = Product.newProduct(
                "Product Name",
                "Product Description",
                10.0,
                10,
                CategoryID.unique(),
                ProductAttributes.create(
                        ProductColor.with("1", "Red"),
                        ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                        "Product Name")
        );

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        aEntity.setAttributes(Set.of());

        final var actualResult = Assertions.assertDoesNotThrow(() -> productRepository.save(aEntity).toDomain());

        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getName(), actualResult.getName());
        Assertions.assertEquals(aEntity.getDescription(), actualResult.getDescription());
        Assertions.assertEquals(aEntity.getPrice(), actualResult.getPrice());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
        Assertions.assertEquals(aEntity.getCategoryId(), actualResult.getCategoryId().getValue());
        Assertions.assertTrue(actualResult.getAttributes().isEmpty());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullSize_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "size";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.product.persistence.ProductSizeJpaEntity.size";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        final var aProductSize = aEntity.getAttributes().stream().findFirst().get().getSize();
        aProductSize.setSize(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullProductSizeId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.product.persistence.ProductSizeJpaEntity";

        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        final var aProductSize = aEntity.getAttributes().stream().findFirst().get().getSize();
        aProductSize.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> productRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidProductSizeValues_whenCallSave_shouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aEntity = ProductJpaEntity.toEntity(aProduct);
        final var aProductSize = aEntity.getAttributes().stream().findFirst().get().getSize();
        aProductSize.setWeight(1.0);
        aProductSize.setHeight(1.0);
        aProductSize.setWidth(1.0);
        aProductSize.setDepth(1.0);
        final var aProductSizeDomain = aProductSize.toDomain();

        Assertions.assertEquals(aProductSize.getId(), aProductSizeDomain.id());
        Assertions.assertEquals(aProductSize.getSize(), aProductSizeDomain.size());
        Assertions.assertEquals(aProductSize.getWeight(), aProductSizeDomain.weight());
        Assertions.assertEquals(aProductSize.getHeight(), aProductSizeDomain.height());
        Assertions.assertEquals(aProductSize.getWidth(), aProductSizeDomain.width());
        Assertions.assertEquals(aProductSize.getDepth(), aProductSizeDomain.depth());

        Assertions.assertEquals(0, productRepository.count());

        final var actualResult = Assertions.assertDoesNotThrow(() -> productRepository.save(aEntity).toDomain());

        Assertions.assertEquals(1, productRepository.count());
        Assertions.assertEquals(aEntity.getId(), actualResult.getId().getValue());
        Assertions.assertEquals(aEntity.getName(), actualResult.getName());
        Assertions.assertEquals(aEntity.getDescription(), actualResult.getDescription());
        Assertions.assertEquals(aEntity.getPrice(), actualResult.getPrice());
        Assertions.assertEquals(aEntity.getQuantity(), actualResult.getQuantity());
        Assertions.assertEquals(aEntity.getCategoryId(), actualResult.getCategoryId().getValue());
        Assertions.assertFalse(actualResult.getAttributes().isEmpty());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualResult.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualResult.getUpdatedAt());
    }
}
