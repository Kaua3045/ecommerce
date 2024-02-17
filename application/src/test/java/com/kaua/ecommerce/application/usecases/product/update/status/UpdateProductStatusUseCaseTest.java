package com.kaua.ecommerce.application.usecases.product.update.status;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
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

public class UpdateProductStatusUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DefaultUpdateProductStatusUseCase updateProductStatusUseCase;

    @Test
    void givenAValidCommandWithInactiveStatus_whenCallExecute_thenShouldUpdateProductStatusToInactive() {
        final var aProduct = Fixture.Products.tshirt();
        final var aStatus = ProductStatus.INACTIVE;

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus.name()
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductStatusUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aProduct.getDescription())
                        && Objects.equals(aCmd.getPrice(), aProduct.getPrice())
                        && Objects.equals(aCmd.getQuantity(), aProduct.getQuantity())
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aProduct.getCategoryId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.equals(aStatus, aCmd.getStatus())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandWithActiveStatus_whenCallExecute_thenShouldUpdateProductStatusToActive() {
        final var aProduct = Fixture.Products.tshirt().updateStatus(ProductStatus.INACTIVE);
        final var aStatus = ProductStatus.ACTIVE;

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus.name()
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        final var aOutput = this.updateProductStatusUseCase.execute(aCommand);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getName(), aProduct.getName())
                        && Objects.equals(aCmd.getDescription(), aProduct.getDescription())
                        && Objects.equals(aCmd.getPrice(), aProduct.getPrice())
                        && Objects.equals(aCmd.getQuantity(), aProduct.getQuantity())
                        && aCmd.getImages().isEmpty()
                        && Objects.equals(aCmd.getCategoryId().getValue(), aProduct.getCategoryId().getValue())
                        && Objects.equals(1, aCmd.getAttributes().size())
                        && Objects.equals(aStatus, aCmd.getStatus())
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidProductId_whenCallExecute_shouldThrowNotFoundException() {
        final var aProductId = "1";
        final var aStatus = ProductStatus.INACTIVE;

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aProductId);

        final var aCommand = UpdateProductStatusCommand.with(
                aProductId,
                aStatus.name()
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.updateProductStatusUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidStatus_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aStatus = "INVALID_STATUS";

        final var expectedErrorMessage = "status %s was not found".formatted(aStatus);

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));

        final var aOutput = Assertions.assertThrows(
                DomainException.class,
                () -> this.updateProductStatusUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidStatusDeleted_whenCallExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        final var aStatus = ProductStatus.DELETED;

        final var expectedErrorMessage = "status DELETED is not allowed to be set";

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus.name()
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));

        final var aOutput = Assertions.assertThrows(
                DomainException.class,
                () -> this.updateProductStatusUseCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(productGateway, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidProductAsMarkedToDeleted_whenCallUpdateProductStatusExecute_shouldThrowDomainException() {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.updateStatus(ProductStatus.DELETED);

        final var aProductId = aProduct.getId().getValue();
        final var aStatus = ProductStatus.ACTIVE;

        final var expectedErrorMessage = "Product with id %s is deleted".formatted(aProductId);

        final var aCommand = UpdateProductStatusCommand.with(
                aProductId,
                aStatus.name()
        );

        Mockito.when(productGateway.findById(aProductId)).thenReturn(Optional.of(aProduct));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.updateProductStatusUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProductId);
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteButThrowsOnPublishEvent_thenShouldThrowTransactionFailureException() {
        final var aProduct = Fixture.Products.tshirt();

        final var aStatus = ProductStatus.INACTIVE;

        final var expectedErrorMessage = "Error on publish event";

        final var aCommand = UpdateProductStatusCommand.with(
                aProduct.getId().getValue(),
                aStatus.name()
        );

        Mockito.when(this.productGateway.findById(Mockito.any())).thenReturn(Optional.of(aProduct));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.updateProductStatusUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(productGateway, Mockito.times(1)).findById(aProduct.getId().getValue());
        Mockito.verify(productGateway, Mockito.times(0)).update(Mockito.any());
    }
}
