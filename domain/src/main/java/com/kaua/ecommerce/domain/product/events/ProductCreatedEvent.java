package com.kaua.ecommerce.domain.product.events;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;

public record ProductCreatedEvent(
        String id,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private ProductCreatedEvent(final String id) {
        this(
                id,
                Product.class.getSimpleName().toLowerCase(),
                EventsTypes.PRODUCT_CREATED,
                InstantUtils.now()
        );
    }

    public static ProductCreatedEvent from(final Product aProduct) {
        return new ProductCreatedEvent(aProduct.getId().getValue());
    }
}
