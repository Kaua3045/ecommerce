package com.kaua.ecommerce.application.usecases.product.attributes.remove;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.exceptions.ProductIsDeletedException;
import com.kaua.ecommerce.application.exceptions.ProductNotHaveMoreAttributesException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.EventPublisher;
import com.kaua.ecommerce.application.gateways.ProductGateway;
import com.kaua.ecommerce.application.gateways.ProductInventoryGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductStatus;
import com.kaua.ecommerce.domain.product.events.ProductUpdatedEvent;

import java.util.Objects;

public class DefaultRemoveProductAttributesUseCase extends RemoveProductAttributesUseCase {

    private final ProductGateway productGateway;
    private final TransactionManager transactionManager;
    private final EventPublisher eventPublisher;
    private final ProductInventoryGateway productInventoryGateway;

    public DefaultRemoveProductAttributesUseCase(
            final ProductGateway productGateway,
            final TransactionManager transactionManager,
            final EventPublisher eventPublisher,
            final ProductInventoryGateway productInventoryGateway
    ) {
        this.productGateway = Objects.requireNonNull(productGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.productInventoryGateway = Objects.requireNonNull(productInventoryGateway);
    }

    @Override
    public void execute(RemoveProductAttributesCommand input) {
        final var aProduct = this.productGateway.findById(input.productId())
                .orElseThrow(NotFoundException.with(Product.class, input.productId()));

        if (aProduct.getStatus().equals(ProductStatus.DELETED)) {
            throw ProductIsDeletedException.with(aProduct.getId());
        }

        if (aProduct.getAttributes().isEmpty()) {
            throw new ProductNotHaveMoreAttributesException();
        }

        final var aResultAfterCallRemove = aProduct.removeAttribute(input.sku());

        if (aResultAfterCallRemove != null) {
            final var aResultOnDeleteInventory = this.productInventoryGateway
                    .deleteInventoryBySku(input.sku());

            if (aResultOnDeleteInventory.isLeft()) {
                throw DomainException.with(aResultOnDeleteInventory.getLeft().getErrors());
            }

            final var aTransactionResult = this.transactionManager.execute(() -> {
                final var aUpdateResult = this.productGateway.update(aResultAfterCallRemove);
                this.eventPublisher.publish(ProductUpdatedEvent.from(aResultAfterCallRemove));
                return aUpdateResult;
            });

            if (aTransactionResult.isFailure()) {
                // TODO: precisamos de uma forma para compensar o pagamento
                // alguma forma de dar rollback, ou buscamos o inventory pra deixar in-memory
                // ou então usamos o inventory movement para restaurar a versão antiga do inventory
                // o inventory movement dai tem um IN, OUT e DELETED status, para sabermos quando
                // um inventory foi removido, e podemos restaurar ele
                throw TransactionFailureException.with(aTransactionResult.getErrorResult());
            }
        }
    }
}
