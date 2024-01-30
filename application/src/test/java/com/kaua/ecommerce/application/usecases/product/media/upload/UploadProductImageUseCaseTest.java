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

import java.util.List;
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
    void givenAValidParamsWithBannerType_whenCallExecuteWithProductContainsBannerImage_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.productImageResource(ProductImageType.BANNER);
        aProduct.changeBannerImage(ProductImage.with(
                "image.png",
                "BANNER-image.png",
                "http://localhost:8080/BANNER-image.png"
        ));

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, List.of(aProductImageResource));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(mediaResourceGateway).clearImage(Mockito.any());
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(aProduct.getId(), aProductImageResource);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(aProductImage.id(), aCmd.getBannerImage().get().id())
                        && Objects.equals(aProductImage.location(), aCmd.getBannerImage().get().location())));
    }

    @Test
    void givenAValidParamsWithBannerType_whenCallExecuteWithProductNotContainsBannerImage_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.productImageResource(ProductImageType.BANNER);

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, List.of(aProductImageResource));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).storeImage(aProduct.getId(), aProductImageResource);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(aProductImage.id(), aCmd.getBannerImage().get().id())
                        && Objects.equals(aProductImage.location(), aCmd.getBannerImage().get().location())));
    }

    @Test
    void givenAValidParamsWithGalleryType_whenCallExecute_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.productImageResource(ProductImageType.GALLERY);

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(aProductId, List.of(aProductImageResource));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
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

        final var aCommand = UploadProductImageCommand.with(aProductId, List.of(aProductImageResource));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.uploadProductImageUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).storeImage(Mockito.any(), Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidTwoGalleryImages_whenCallUploadProductImageExecute_shouldUploadProductImage() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        final var aProductImageTwo = ProductImage.with(
                "image2.png",
                "GALLERY-image2.png",
                "http://localhost:8080/GALLERY-image2.png"
        );
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.productImageResource(ProductImageType.GALLERY);
        final var aProductImageResourceTwo = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image2.png"
                ),
                ProductImageType.GALLERY
        );

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = UploadProductImageCommand.with(
                aProductId,
                List.of(aProductImageResource, aProductImageResourceTwo)
        );

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(mediaResourceGateway.storeImage(Mockito.any(), Mockito.any()))
                .thenReturn(aProductImage)
                .thenReturn(aProductImageTwo);
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());

        final var aOutput = this.uploadProductImageUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(2)).storeImage(Mockito.any(), Mockito.any());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(2, aCmd.getImages().size())));
    }

    @Test
    void givenAnInvalidTwoBannerImages_whenCallUploadProductImageExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductImageResource = Fixture.Products.productImageResource(ProductImageType.BANNER);
        final var aProductImageResourceTwo = ProductImageResource.with(
                Resource.with(
                        "content".getBytes(),
                        "image/png",
                        "image2.png"
                ),
                ProductImageType.BANNER
        );

        final var aProductId = aProduct.getId().getValue();

        final var expectedErrorMessage = "Only one banner image is allowed";

        final var aCommand = UploadProductImageCommand.with(
                aProductId,
                List.of(aProductImageResource, aProductImageResourceTwo)
        );

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.uploadProductImageUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).storeImage(Mockito.any(), Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }
}
