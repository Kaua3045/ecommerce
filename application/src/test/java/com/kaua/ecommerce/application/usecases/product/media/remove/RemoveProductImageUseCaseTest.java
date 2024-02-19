package com.kaua.ecommerce.application.usecases.product.media.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.MediaResourceGateway;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class RemoveProductImageUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DefaultRemoveProductImageUseCase removeProductImageUseCase;

    @Test
    void givenAValidParamsWithLocationIsBanner_whenCallExecute_shouldBeOk() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(aProductImage);

        final var aProductId = aProduct.getId().getValue();

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aCommand = RemoveProductImageCommand.with(aProductId, aProductImage.getLocation());

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(mediaResourceGateway).clearImage(Mockito.any());
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.removeProductImageUseCase.execute(aCommand));

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && aCmd.getBannerImage().isEmpty()
                        && aProductUpdatedAt.isBefore(aCmd.getUpdatedAt())));
        Mockito.verify(this.transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    void givenAValidProductIdButLocationNotMatches_whenCallExecute_shouldBeOk() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(ProductImage.with(
                "another-name",
                "another-location",
                "another-url"
        ));

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = RemoveProductImageCommand.with(aProductId, aProductImage.getLocation());

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        Assertions.assertDoesNotThrow(() -> this.removeProductImageUseCase.execute(aCommand));

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(this.transactionManager, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(0)).publish(Mockito.any());
    }

    @Test
    void givenAValidParamsWithLocationIsGallery_whenCallExecute_shouldBeOk() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.addImage(aProductImage);

        final var aProductId = aProduct.getId().getValue();

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aCommand = RemoveProductImageCommand.with(aProductId, aProductImage.getLocation());

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.doNothing().when(mediaResourceGateway).clearImage(Mockito.any());
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.removeProductImageUseCase.execute(aCommand));

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(1)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && aCmd.getBannerImage().isEmpty()
                        && aCmd.getImages().isEmpty()
                        && aProductUpdatedAt.isBefore(aCmd.getUpdatedAt())));
        Mockito.verify(this.transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallExecute_shouldThrowNotFoundException() {
        final var aProductId = "a-product-id";

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        final var aCommand = RemoveProductImageCommand.with(aProductId, "a-location");

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.removeProductImageUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidProductAsMarkedToDeleted_whenCallRemoveProductImageExecute_shouldThrowDomainException() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(aProductImage);
        aProduct.updateStatus(ProductStatus.DELETED);

        final var aProductId = aProduct.getId().getValue();

        final var expectedErrorMessage = "Product with id %s is deleted".formatted(aProductId);

        final var aCommand = RemoveProductImageCommand.with(
                aProductId,
                aProductImage.getLocation()
        );

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.removeProductImageUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(mediaResourceGateway, Mockito.times(0)).clearImage(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommandWithBannerLocation_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(aProductImage);

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = RemoveProductImageCommand.with(
                aProductId,
                aProductImage.getLocation()
        );

        final var expectedErrorMessage = "Error on publish event";

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.removeProductImageUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommandWitGalleryLocation_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.GALLERY);
        final var aProduct = Fixture.Products.tshirt();
        aProduct.addImage(aProductImage);

        final var aProductId = aProduct.getId().getValue();

        final var aCommand = RemoveProductImageCommand.with(
                aProductId,
                aProductImage.getLocation()
        );

        final var expectedErrorMessage = "Error on publish event";

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.removeProductImageUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }
}
