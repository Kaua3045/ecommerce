package com.kaua.ecommerce.domain.product.events;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;

public record ProductDeletedEvent(
        String id,
        String eventId,
        String aggregateName,
        String eventType,
        Instant occurredOn,
        long version
) implements DomainEvent {

    private ProductDeletedEvent(final String id, final long version) {
        this(
                id,
                IdUtils.generate(),
                Product.class.getSimpleName().toLowerCase(),
                EventsTypes.PRODUCT_DELETED,
                InstantUtils.now(),
                version
        );
    }

    public static ProductDeletedEvent from(final Product aProduct) {
        return new ProductDeletedEvent(aProduct.getId().getValue(), aProduct.getVersion());
    }
}
