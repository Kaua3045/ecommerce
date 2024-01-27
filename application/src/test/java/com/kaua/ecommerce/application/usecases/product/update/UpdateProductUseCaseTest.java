package com.kaua.ecommerce.application.usecases.product.update;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UpdateProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateProductUseCase updateProductUseCase;

    @Test
    void givenAValidCommand_whenCallExecute_thenShouldUpdateProduct() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(55.86);
        final var aQuantity = 3;
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidProductId_whenCallExecute_shouldThrowNotFoundException() {
        final var aCategory = Fixture.Categories.tech();

        final var aProductId = "1";
        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(9.0);
        final var aQuantity = 50;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        final var aCommand = UpdateProductCommand.with(
                aProductId,
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aQuantity = 10;
        final var aCategoryId = "1";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(256);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aProduct.getPrice())
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
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
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
    void givenAnInvalidQuantityLessThanZero_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aProduct = Fixture.Products.tshirt();

        final var aName = "Product Name";
        final var aDescription = "Product Description";
        final var aPrice = BigDecimal.valueOf(5.0);
        final var aQuantity = -1;
        final var aCategoryId = aCategory.getId().getValue();

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aQuantity = 10;
        final String aCategoryId = null;

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
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
        final var aQuantity = 10;
        final var aCategoryId = " ";

        final var aCommand = UpdateProductCommand.with(
                aProduct.getId().getValue(),
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aProduct.getCategoryId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }
}
