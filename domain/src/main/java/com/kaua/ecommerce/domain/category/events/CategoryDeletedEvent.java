package com.kaua.ecommerce.domain.category.events;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public record CategoryDeletedEvent(
        String rootCategoryId,
        Optional<String> subCategoryId,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private CategoryDeletedEvent(final String rootCategoryId, final String subCategoryId) {
        this(
                rootCategoryId,
                Optional.ofNullable(subCategoryId),
                Category.class.getSimpleName().toLowerCase(),
                EventsTypes.CATEGORY_DELETED,
                InstantUtils.now()
        );
    }

    public static CategoryDeletedEvent from(final String rootCategoryId, final String subCategoryId) {
        return new CategoryDeletedEvent(rootCategoryId, subCategoryId);
    }
}
