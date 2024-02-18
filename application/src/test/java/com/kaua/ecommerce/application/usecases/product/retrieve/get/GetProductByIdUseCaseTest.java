package com.kaua.ecommerce.application.usecases.product.retrieve.get;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetProductByIdUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @InjectMocks
    private DefaultGetProductByIdUseCase getProductByIdUseCase;

    @Test
    void givenAValidProductId_whenCallExecute_thenShouldReturnProduct() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        final var aId = aProduct.getId().getValue();

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));

        final var aOutput = this.getProductByIdUseCase.execute(aId);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProduct.getId().getValue(), aOutput.productId());
        Assertions.assertEquals(aProduct.getName(), aOutput.name());
        Assertions.assertEquals(aProduct.getDescription(), aOutput.description());
        Assertions.assertEquals(aProduct.getPrice(), aOutput.price());
        Assertions.assertEquals(aProduct.getQuantity(), aOutput.quantity());
        Assertions.assertEquals(aProduct.getCategoryId().getValue(), aOutput.categoryId());
        Assertions.assertNotNull(aOutput.bannerImage());
        Assertions.assertEquals(aProduct.getBannerImage().get().getLocation(), aOutput.bannerImage().location());
        Assertions.assertNotNull(aOutput.galleryImages());
        Assertions.assertEquals(aOutput.galleryImages().size(), aProduct.getImages().size());
        Assertions.assertEquals(aOutput.attributes().size(), aProduct.getAttributes().size());
        Assertions.assertEquals(aProduct.getStatus().name(), aOutput.status());
        Assertions.assertEquals(aProduct.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aProduct.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aProduct.getVersion(), aOutput.version());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
    }

    @Test
    void givenAnInvalidProductId_whenCallExecute_shouldThrowNotFoundException() {
        final var aProductId = "1";

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.getProductByIdUseCase.execute(aProductId)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
    }
}
