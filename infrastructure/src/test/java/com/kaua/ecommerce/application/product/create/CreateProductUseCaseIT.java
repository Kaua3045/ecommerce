package com.kaua.ecommerce.application.product.create;

import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommandAttributes;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@IntegrationTest
public class CreateProductUseCaseIT {

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAValidValues_whenCallsCreateProductUseCase_thenProductShouldBeCreated() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = new BigDecimal("10.00");
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "RED";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Assertions.assertEquals(0, this.productRepository.count());

        final var aResult = this.createProductUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(1, this.productRepository.count());

        Assertions.assertNotNull(aResult.id());

        final var aPersistedProduct = this.productRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aResult.id(), aPersistedProduct.getId());
        Assertions.assertEquals(aName, aPersistedProduct.getName());
        Assertions.assertEquals(aDescription, aPersistedProduct.getDescription());
        Assertions.assertEquals(aPrice, aPersistedProduct.getPrice());
        Assertions.assertEquals(aCategoryId, aPersistedProduct.getCategoryId());
        Assertions.assertEquals(aColor, aPersistedProduct.getAttributes().stream().findFirst().get().getColor().getColor());
        Assertions.assertEquals(aSize, aPersistedProduct.getAttributes().stream().findFirst().get().getSize().getSize());
        Assertions.assertNotNull(aPersistedProduct.getCreatedAt());
        Assertions.assertNotNull(aPersistedProduct.getUpdatedAt());
    }

    @Test
    void givenAnInvalidName_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = " ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Pr ";
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = RandomStringUtils.generateValue(256);
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan3000_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAnInvalidPriceSmallerOrEqualZero_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(0.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallsCreateProductUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = -1;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "Red";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var aResult = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.productRepository.count());
    }

    @Test
    void givenAValidValues_whenCallsCreateProductUseCaseButThrowsTransactionException_shouldReturnTransactionFailureException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = new BigDecimal("12345678901234567890.123");
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColor = "RED";
        final var aSize = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColor,
                aSize,
                aWeight,
                aHeight,
                aWidth,
                aDepth,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Assertions.assertEquals(0, this.productRepository.count());

        final var aResult = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.createProductUseCase.execute(aCommand)
        );

        Assertions.assertEquals(0, this.productRepository.count());
        Assertions.assertEquals(0, this.inventoryRepository.count());

        Assertions.assertNotNull(aResult.getMessage());
    }
}
