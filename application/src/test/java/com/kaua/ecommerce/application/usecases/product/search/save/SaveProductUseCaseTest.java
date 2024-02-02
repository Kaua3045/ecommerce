package com.kaua.ecommerce.application.usecases.product.search.save;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductAttributes;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;

public class SaveProductUseCaseTest extends UseCaseTest {

    @Mock
    private SearchGateway<Product> productSearchGateway;

    @InjectMocks
    private SaveProductUseCase useCase;

    @Test
    void givenAValidProduct_whenCallSave_shouldPersistProduct() {
        final var aProduct = Fixture.Products.book();

        Mockito.when(productSearchGateway.save(Mockito.any())).thenAnswer(returnsFirstArg());

        this.useCase.execute(aProduct);

        Mockito.verify(productSearchGateway, Mockito.times(1)).save(eq(aProduct));
    }

    @Test
    void givenAnInvalidName_whenCallSave_shouldThrowsDomainException() {
        final var aProduct = Product.newProduct(
                " ",
                "A good book",
                BigDecimal.valueOf(10.0),
                10,
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with("Red"),
                        ProductSize.with("M", 10.0, 10.0, 10.0, 10.0),
                        " "
                ))
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorCount = 1;

        final var actualError = Assertions.assertThrows(DomainException.class,
                () -> this.useCase.execute(aProduct));

        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());

        Mockito.verify(productSearchGateway, Mockito.times(0)).save(eq(aProduct));
    }

    @Test
    void givenAnInvalidNullCategory_whenCallSave_shouldThrowsDomainException() {
        final Product aProduct = null;

        final var expectedErrorMessage = "Product cannot be null";
        final var expectedErrorCount = 1;

        final var actualError = Assertions.assertThrows(DomainException.class,
                () -> this.useCase.execute(aProduct));

        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());

        Mockito.verify(productSearchGateway, Mockito.times(0)).save(eq(aProduct));
    }
}
