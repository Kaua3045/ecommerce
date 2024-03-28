package com.kaua.ecommerce.application.usecases.product.retrieve.details;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetProductDetailsBySkuUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @InjectMocks
    private DefaultGetProductDetailsBySkuUseCase getProductDetailsBySkuUseCase;

    @Test
    void givenAValidSku_whenCallExecute_thenShouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();

        final var aAttribute = aProduct.getAttributes().stream().findFirst().get();
        final var aSku = aAttribute.getSku();

        final var aProductDetails = new ProductDetails(
                aSku,
                aProduct.getPrice(),
                aAttribute.getSize().getWeight(),
                aAttribute.getSize().getWidth(),
                aAttribute.getSize().getHeight(),
                aAttribute.getSize().getLength()
        );

        Mockito.when(this.productGateway.findProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(aProductDetails));

        final var aOutput = this.getProductDetailsBySkuUseCase.execute(aSku);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProductDetails.sku(), aOutput.sku());
        Assertions.assertEquals(aProductDetails.price(), aOutput.price());
        Assertions.assertEquals(aProductDetails.weight(), aOutput.weight());
        Assertions.assertEquals(aProductDetails.width(), aOutput.width());
        Assertions.assertEquals(aProductDetails.height(), aOutput.height());
        Assertions.assertEquals(aProductDetails.length(), aOutput.length());

        Mockito.verify(productGateway, Mockito.times(1)).findProductDetailsBySku(aSku);
    }

    @Test
    void givenAnInvalidSku_whenCallExecute_shouldThrowNotFoundException() {
        final var aSku = Fixture.createSku("invalid-sku");

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aSku);

        Mockito.when(this.productGateway.findProductDetailsBySku(Mockito.any())).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.getProductDetailsBySkuUseCase.execute(aSku)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findProductDetailsBySku(aSku);
    }
}
