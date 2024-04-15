package com.kaua.ecommerce.domain.inventory.events;

import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.List;

public record InventoryCreatedRollbackBySkusEvent(
        List<String> skus,
        String eventId,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private InventoryCreatedRollbackBySkusEvent(final List<String> skus) {
        this(
                skus,
                IdUtils.generate(),
                Inventory.class.getSimpleName().toLowerCase(),
                EventsTypes.INVENTORY_CREATED_ROLLBACK_BY_SKUS,
                InstantUtils.now()
        );
    }

    public static InventoryCreatedRollbackBySkusEvent from(final List<String> skus) {
        return new InventoryCreatedRollbackBySkusEvent(skus);
    }
}
