package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ProductInventoryGateway productInventoryGateway;

    @InjectMocks
    private DefaultCreateProductUseCase createProductUseCase;

    @Test
    void givenAValidCommand_whenCallExecute_thenShouldCreateProduct() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "RED";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productInventoryGateway.createInventory(Mockito.any(), Mockito.any()))
                .thenReturn(Either.right(null));
        Mockito.when(this.productGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.createProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).create(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(aColorName, aCmd.getAttributes().stream().findFirst().get().getColor().getColor())
                        && Objects.equals(aSizeName, aCmd.getAttributes().stream().findFirst().get().getSize().getSize())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
        Mockito.verify(transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .createInventory(Mockito.any(), Mockito.any());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallExecute_shouldThrowNotFoundException() {
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "1";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.createProductUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankName_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = " ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullName_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final String aName = null;
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Pr ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = RandomStringUtils.generateValue(256);
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan3000_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullPrice_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final BigDecimal aPrice = null;
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("price");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidPriceSmallerOrEqualZero_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(00.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(5.0);
        final var aQuantity = -1;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productInventoryGateway.createInventory(Mockito.any(), Mockito.any()))
                .thenReturn(Either.left(NotificationHandler
                        .create(new Error(CommonErrorMessage.greaterThan("quantity", -1)))));

        final var aOutput = this.createProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "RED";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributes)
        );

        final var expectedErrorMessage = "Event Publisher Error";

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productInventoryGateway.createInventory(Mockito.any(), Mockito.any()))
                .thenReturn(Either.right(null));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));
        Mockito.when(this.productInventoryGateway.cleanInventoriesByProductId(Mockito.any()))
                .thenReturn(Either.right(null));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.createProductUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .createInventory(Mockito.any(), Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(eventPublisher, Mockito.times(0)).publish(Mockito.any());
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .cleanInventoriesByProductId(Mockito.any());
    }
}
