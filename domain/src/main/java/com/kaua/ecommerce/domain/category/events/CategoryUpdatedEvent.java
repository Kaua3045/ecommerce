package com.kaua.ecommerce.domain.category.events;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record CategoryUpdatedEvent(
        String id,
        String name,
        String description,
        String slug,
        String parentId,
        Set<CategoryUpdatedEvent> subCategories,
        int level,
        Instant createdAt,
        Instant updatedAt,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private CategoryUpdatedEvent(
            final String id,
            final String name,
            final String description,
            final String slug,
            final String parentId,
            final Set<CategoryUpdatedEvent> subCategories,
            final int level,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this(
                id,
                name,
                description,
                slug,
                parentId,
                subCategories,
                level,
                createdAt,
                updatedAt,
                Category.class.getSimpleName().toLowerCase(),
                EventsTypes.CATEGORY_UPDATED,
                InstantUtils.now()
        );
    }

    public static CategoryUpdatedEvent from(final Category aCategory) {
        return new CategoryUpdatedEvent(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getSubCategories().stream()
                        .map(CategoryUpdatedEvent::from).collect(Collectors.toSet()),
                aCategory.getLevel(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );
    }

    public Category toDomain() {
        return Category.with(
                id(),
                name(),
                description(),
                slug(),
                parentId(),
                subCategories.stream().map(CategoryUpdatedEvent::toDomain).collect(Collectors.toSet()),
                level(),
                createdAt(),
                updatedAt(),
                null
        );
    }
}
