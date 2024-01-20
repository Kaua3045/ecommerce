package com.kaua.ecommerce.application.usecases.product.media.upload;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductImageResource;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.utils.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UploadProductImageUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultUploadProductImageUseCase uploadProductImageUseCase;

    @Test
    void givenAValidParamsWithCoverType_whenCallExecuteWithProductContainsCoverImage_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.imageCoverType();
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.imageCoverTypeResource();
        aProduct.changeCoverImage(ProductImage.with(
                "image.png",
                "COVER-image.png",
                "http://localhost:8080/COVER-image.png"
        ));

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, aProductImageResource);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(mediaResourceGateway).clearImage(Mockito.any());
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(ProductImageType.COVER.name(), aOutput.productImageType().name());
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(aProduct.getId(), aProductImageResource);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(aProductImage.id(), aCmd.getCoverImage().get().id())
                        && Objects.equals(aProductImage.location(), aCmd.getCoverImage().get().location())));
    }

    @Test
    void givenAValidParamsWithCoverType_whenCallExecuteWithProductNotContainsCoverImage_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.imageCoverType();
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.imageCoverTypeResource();

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, aProductImageResource);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(ProductImageType.COVER.name(), aOutput.productImageType().name());
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(aProduct.getId(), aProductImageResource);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(aProductImage.id(), aCmd.getCoverImage().get().id())
                        && Objects.equals(aProductImage.location(), aCmd.getCoverImage().get().location())));
    }

    @Test
    void givenAValidParamsWithGalleryType_whenCallExecute_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.imageGalleryType();
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.imageGalleryTypeResource();

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, aProductImageResource);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(ProductImageType.GALLERY.name(), aOutput.productImageType().name());
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(aProduct.getId(), aProductImageResource);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(1, aCmd.getImages().size())
                        && Objects.equals(aProductImage.id(), aCmd.getImages().stream().findFirst().get().id())
                        && Objects.equals(aProductImage.name(), aCmd.getImages().stream().findFirst().get().name())
                        && Objects.equals(aProductImage.location(), aCmd.getImages().stream().findFirst().get().location())));
    }

    @Test
    void givenAnInvalidType_whenCallUploadProductImageExecute_shouldThrowNotFoundException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image.png"
                ),
                ProductImageType.INVALID_TYPE
        );

        final var aProductId = aProduct.getId().getValue();

        final var expectedErrorMessage = "'productImageType' not implemented";

        final var aCommand = UploadProductImageCommand.with(aProductId, aProductImageResource);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.uploadProductImageUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).storeImage(Mockito.any(), Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }
}
