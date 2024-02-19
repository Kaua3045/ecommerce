package com.kaua.ecommerce.application.usecases.product.attributes.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.DomainException;
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

public class RemoveProductAttributesUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DefaultRemoveProductAttributesUseCase removeProductAttributesUseCase;

    @Test
    void givenAValidParams_whenCallExecute_shouldRemoveProductAttribute() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.addAttribute(aProductAttribute);

        final var aProductId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        final var aProductUpdatedAt = aProduct.getUpdatedAt();

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(productGateway.update(aProduct)).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.removeProductAttributesUseCase.execute(aCommand));

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aProductId, aCmd.getId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && aProductUpdatedAt.isBefore(aCmd.getUpdatedAt())));
        Mockito.verify(this.transactionManager, Mockito.times(1)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    void givenAnInvalidProductAsMarkedToDeleted_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.updateStatus(ProductStatus.DELETED);
        aProduct.addAttribute(aProductAttribute);

        final var aProductId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        final var expectedErrorMessage = "Product with id %s is deleted".formatted(aProductId);

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.removeProductAttributesUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidProductNotHaveMoreAttributes_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.removeAttribute(aProduct.getAttributes().stream().findFirst().get().getSku());

        final var aProductId = aProduct.getId().getValue();
        final var aSku = "aaa";

        final var expectedErrorMessage = "Product not have more attributes";

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.removeProductAttributesUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.addAttribute(aProductAttribute);

        final var aProductId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        final var expectedErrorMessage = "Error on publish event";

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.removeProductAttributesUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidSku_whenCallExecute_shouldBeOk() {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aProductId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        final var aCommand = RemoveProductAttributesCommand.with(aProductId, aSku);

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        Assertions.assertDoesNotThrow(() -> this.removeProductAttributesUseCase.execute(aCommand));

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(this.transactionManager, Mockito.times(0)).execute(Mockito.any());
        Mockito.verify(this.eventPublisher, Mockito.times(0)).publish(Mockito.any());
    }
}
