package com.kaua.ecommerce.application.usecases.product.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.product.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class DeleteProductUseCaseTest extends UseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private EventPublisher eventPublisher;

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
        Mockito.when(this.productGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });
        Mockito.doNothing().when(this.eventPublisher).publish(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductID));

        Mockito.verify(this.productGateway, Mockito.times(1)).findById(aProductID);
        Mockito.verify(this.productGateway, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(ProductStatus.DELETED, cmd.getStatus())));
    }

    @Test
    void givenAnInvalidProductId_whenDeleteProduct_thenShouldBeOk() {
        final var aProductID = "aProductID";

        Mockito.when(this.productGateway.findById(aProductID)).thenReturn(Optional.empty());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        Assertions.assertDoesNotThrow(() -> this.deleteProductUseCase.execute(aProductID));

        Mockito.verify(this.productGateway, Mockito.times(1)).findById(aProductID);
        Mockito.verify(this.productGateway, Mockito.never()).update(Mockito.any());
    }
}
