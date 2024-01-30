package com.kaua.ecommerce.domain.product.events;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;

public record ProductUpdatedEvent(
        String id,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private ProductUpdatedEvent(final String id) {
        this(
                id,
                Product.class.getSimpleName().toLowerCase(),
                EventsTypes.PRODUCT_UPDATED,
                InstantUtils.now()
        );
    }

    public static ProductUpdatedEvent from(final Product aProduct) {
        return new ProductUpdatedEvent(aProduct.getId().getValue());
    }
}
