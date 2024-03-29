package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.application.usecases.product.retrieve.details.GetProductDetailsBySkuUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

@IntegrationTest
public class OrderProductGatewayImplTest {

    @Autowired
    private OrderProductGatewayImpl orderProductGatewayImpl;

    @MockBean
    private GetProductDetailsBySkuUseCase getProductDetailsBySkuUseCase;

    @Test
    void givenAValidSku_whenGetProductDetailsBySku_thenReturnProductDetails() {
        final var aProduct = Fixture.Products.book();
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

        when(getProductDetailsBySkuUseCase.execute(aSku)).thenReturn(aProductDetails);

        final var aOutput = this.orderProductGatewayImpl.getProductDetailsBySku(aSku);

        Assertions.assertTrue(aOutput.isPresent());
        Assertions.assertEquals(aProductDetails.sku(), aOutput.get().sku());
        Assertions.assertEquals(aProductDetails.price(), aOutput.get().price());
        Assertions.assertEquals(aProductDetails.weight(), aOutput.get().weight());
        Assertions.assertEquals(aProductDetails.width(), aOutput.get().width());
        Assertions.assertEquals(aProductDetails.height(), aOutput.get().height());
        Assertions.assertEquals(aProductDetails.length(), aOutput.get().length());
    }

    @Test
    void givenAnInvalidSku_whenGetProductDetailsBySku_thenReturnEmpty() {
        final var aSku = "invalid-sku";

        when(getProductDetailsBySkuUseCase.execute(aSku))
                .thenThrow(NotFoundException.with(Product.class, aSku).get());

        final var aOutput = this.orderProductGatewayImpl.getProductDetailsBySku(aSku);

        Assertions.assertTrue(aOutput.isEmpty());
    }
}
