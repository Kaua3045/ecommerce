package com.kaua.ecommerce.domain.inventory.events;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;

public record InventoryCreatedRollbackBySkuEvent(
        String sku,
        String eventId,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private InventoryCreatedRollbackBySkuEvent(final String sku) {
        this(
                sku,
                IdUtils.generate(),
                Inventory.class.getSimpleName().toLowerCase(),
                EventsTypes.INVENTORY_CREATED_ROLLBACK_BY_SKU,
                InstantUtils.now()
        );
    }

    public static InventoryCreatedRollbackBySkuEvent from(final String sku) {
        return new InventoryCreatedRollbackBySkuEvent(sku);
    }
}
