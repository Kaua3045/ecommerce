package com.kaua.ecommerce.application.usecases.product.attributes.add;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class AddProductAttributesUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ProductInventoryGateway productInventoryGateway;

    @InjectMocks
    private DefaultAddProductAttributesUseCase addProductAttributesUseCase;

    @Test
    void givenAValidParams_whenCallExecute_shouldAddProductAttribute() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductId = aProduct.getId().getValue();

        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        final var aQuantity = 5;

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aCommand = AddProductAttributesCommand.with(
                aProductId, List.of(new AddProductAttributesCommandParams(
                        aProductAttribute.getColor().getColor(),
                        aProductAttribute.getSize().getSize(),
                        aProductAttribute.getSize().getWeight(),
                        aProductAttribute.getSize().getHeight(),
                        aProductAttribute.getSize().getWidth(),
                        aProductAttribute.getSize().getDepth(),
                        aQuantity
                )));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(productInventoryGateway.createInventory(Mockito.any(), Mockito.anyList()))
                .thenReturn(Either.right(null));
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.addProductAttributesUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aProductId, aOutput.productId());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .createInventory(Mockito.any(), Mockito.anyList());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(2, aCmd.getAttributes().size())
                        && aProductUpdatedAt.isBefore(aCmd.getUpdatedAt())));
        Mockito.verify(this.transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    void givenAnInvalidProductAsMarkedToDeleted_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.updateStatus(ProductStatus.DELETED);

        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aProductId = aProduct.getId().getValue();
        final var aQuantity = 5;

        final var expectedErrorMessage = "Product with id %s is deleted".formatted(aProductId);

        final var aCommand = AddProductAttributesCommand.with(
                aProductId, List.of(new AddProductAttributesCommandParams(
                        aProductAttribute.getColor().getColor(),
                        aProductAttribute.getSize().getSize(),
                        aProductAttribute.getSize().getWeight(),
                        aProductAttribute.getSize().getHeight(),
                        aProductAttribute.getSize().getWidth(),
                        aProductAttribute.getSize().getDepth(),
                        aQuantity
                )));

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.addProductAttributesUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidAttributesLength_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();

        final var aProductId = aProduct.getId().getValue();

        final var expectedErrorMessage = "'attributes' must have at least one element";

        final var aCommand = AddProductAttributesCommand.with(
                aProductId, List.of());

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.addProductAttributesUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButOnCreateInventoriesThrows_thenShouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aProductId = aProduct.getId().getValue();
        final var aQuantity = 5;

        final var aCommand = AddProductAttributesCommand.with(
                aProductId, List.of(new AddProductAttributesCommandParams(
                        aProductAttribute.getColor().getColor(),
                        aProductAttribute.getSize().getSize(),
                        aProductAttribute.getSize().getWeight(),
                        aProductAttribute.getSize().getHeight(),
                        aProductAttribute.getSize().getWidth(),
                        aProductAttribute.getSize().getDepth(),
                        aQuantity
                )));

        final var expectedErrorMessage = "Error on create inventories";

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(productInventoryGateway.createInventory(Mockito.any(), Mockito.anyList()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.addProductAttributesUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .createInventory(Mockito.any(), Mockito.anyList());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aProductId = aProduct.getId().getValue();
        final var aQuantity = 5;

        final var aCommand = AddProductAttributesCommand.with(
                aProductId, List.of(new AddProductAttributesCommandParams(
                        aProductAttribute.getColor().getColor(),
                        aProductAttribute.getSize().getSize(),
                        aProductAttribute.getSize().getWeight(),
                        aProductAttribute.getSize().getHeight(),
                        aProductAttribute.getSize().getWidth(),
                        aProductAttribute.getSize().getDepth(),
                        aQuantity
                )));

        final var expectedErrorMessage = "Error on publish event";

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(productInventoryGateway.createInventory(Mockito.any(), Mockito.anyList()))
                .thenReturn(Either.right(null));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.addProductAttributesUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productInventoryGateway, Mockito.times(1))
                .createInventory(Mockito.any(), Mockito.anyList());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(eventPublisher, Mockito.times(1)).publish(Mockito.any());
    }
}
