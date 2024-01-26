package com.kaua.ecommerce.application.usecases.product.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private CategoryGateway categoryGateway;

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
        final var aDepth = 0.5;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                List.of(aAttributes)
        );

        Mockito.when(this.categoryGateway.findById(aCategoryId)).thenReturn(Optional.of(aCategory));
        Mockito.when(this.productGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createProductUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aCategoryId);
        Mockito.verify(productGateway, Mockito.times(1)).create(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aName)
                        && Objects.equals(aCmd.getDescription(), aDescription)
                        && Objects.equals(aCmd.getPrice(), aPrice)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aCategoryId)
                        && Objects.equals(aColorName, aCmd.getAttributes().stream().findFirst().get().color().color())
                        && Objects.equals(aSizeName, aCmd.getAttributes().stream().findFirst().get().size().size())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallExecute_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(256);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = aCategory.getId().getValue();
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("price");
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aAttributes = CreateProductCommandAttributes.with(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        final var aCommand = CreateProductCommand.with(
                aName,
                aDescription,
                aPrice,
                aQuantity,
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
}
