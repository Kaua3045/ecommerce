package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UpdateProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DefaultUpdateProductUseCase updateProductUseCase;

    @Test
    void givenAValidCommand_whenCallExecute_thenShouldUpdateProduct() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(55.86);
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(transactionManager, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallExecute_shouldThrowNotFoundException() {
        final var aCategory = Fixture.Categories.tech();

        final var aProductId = "1";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(9.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        final var aCommand = UpdateProductCommand.with(
                aProductId,
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.updateProductUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallExecute_shouldThrowNotFoundException() {
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = "1";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.updateProductUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankName_whenCallExecute_shouldReturnOldProductName() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = " ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidNullName_whenCallExecute_shouldReturnOldProductName() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final String aName = null;
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Pr ";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.updateProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = RandomStringUtils.generateValue(256);
        final String aDescription = null;
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.updateProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan3000_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.updateProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidNullPrice_whenCallExecute_shouldReturnOldPrice() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final BigDecimal aPrice = null;
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aProduct.getPrice())
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidPriceSmallerOrEqualZero_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(00.0);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));

        final var aOutput = this.updateProductUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidNullCategory_whenCallExecute_shouldReturnOldCategoryId() {
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(5.0);
        final String aCategoryId = null;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aProduct.getCategoryId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidBlankCategory_whenCallExecute_shouldReturnOldCategoryId() {
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(5.0);
        final var aCategoryId = " ";

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aProduct.getCategoryId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidProductAsMarkedToDeleted_whenCallUpdateProductExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.updateStatus(ProductStatus.DELETED);

        final var aProductId = aProduct.getId().getValue();

        final var expectedErrorMessage = "Product with id %s is deleted".formatted(aProductId);

        final var aCommand = UpdateProductCommand.with(
                aProductId,
                "Camiseta larga",
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getCategoryId().getValue()
        );

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.updateProductUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(55.86);
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = "Error on publish event";

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.updateProductUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }
}
