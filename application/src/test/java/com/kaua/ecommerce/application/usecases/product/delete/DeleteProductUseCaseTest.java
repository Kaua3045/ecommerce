package com.kaua.ecommerce.application.usecases.product.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class DeleteProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultDeleteProductUseCase deleteProductUseCase;

    @Test
    void givenAValidProductId_whenDeleteProduct_thenShouldDeleteProduct() {
        final var aProduct = Product.newProduct(
                "aName",
                "aDescription",
                new BigDecimal("10.00"),
                5,
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with("red"),
                        ProductSize.with(
                                "XG",
                                10.0,
                                10.0,
                                10.0,
                                10.0),
                        "aName"
                ))
        );
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        final var aProductID = aProduct.getId().getValue();

        Mockito.when(this.productGateway.findById(aProductID)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(this.productGateway).delete(aProductID);
        Mockito.doNothing().when(this.mediaResourceGateway).clearImages(aProduct.getImages());
        Mockito.doNothing().when(this.mediaResourceGateway).clearImage(aProduct.getBannerImage().get());

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductID));

        Mockito.verify(this.productGateway, Mockito.times(1)).findById(aProductID);
        Mockito.verify(this.productGateway, Mockito.times(1)).delete(aProductID);
        Mockito.verify(this.mediaResourceGateway, Mockito.times(1)).clearImages(aProduct.getImages());
        Mockito.verify(this.mediaResourceGateway, Mockito.times(1)).clearImage(aProduct.getBannerImage().get());
    }

    @Test
    void givenAnInvalidProductId_whenDeleteProduct_thenShouldBeOk() {
        final var aProductID = "aProductID";

        Mockito.when(this.productGateway.findById(aProductID)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductID));

        Mockito.verify(this.productGateway, Mockito.times(1)).findById(aProductID);
        Mockito.verify(this.productGateway, Mockito.never()).delete(aProductID);
        Mockito.verify(this.mediaResourceGateway, Mockito.never()).clearImages(Mockito.any());
        Mockito.verify(this.mediaResourceGateway, Mockito.never()).clearImage(Mockito.any());
    }

    @Test
    void givenAValidProductIdAndProductWithoutBannerImage_whenDeleteProduct_thenShouldDeleteProduct() {
        final var aProduct = Product.newProduct(
                "aName",
                "aDescription",
                new BigDecimal("10.00"),
                5,
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with("red"),
                        ProductSize.with(
                                "XG",
                                10.0,
                                10.0,
                                10.0,
                                10.0),
                        "aName"
                ))
        );
        final var aProductID = aProduct.getId().getValue();

        Mockito.when(this.productGateway.findById(aProductID)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(this.productGateway).delete(aProductID);

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductID));

        Mockito.verify(this.productGateway, Mockito.times(1)).findById(aProductID);
        Mockito.verify(this.productGateway, Mockito.times(1)).delete(aProductID);
        Mockito.verify(this.mediaResourceGateway, Mockito.times(0)).clearImages(aProduct.getImages());
        Mockito.verify(this.mediaResourceGateway, Mockito.never()).clearImage(Mockito.any());
    }
}
