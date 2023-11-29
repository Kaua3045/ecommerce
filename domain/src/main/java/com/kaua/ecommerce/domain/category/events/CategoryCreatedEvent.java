package com.kaua.ecommerce.domain.category.events;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.event.EventsTypes;
import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record CategoryCreatedEvent(
        String id,
        String name,
        String description,
        String slug,
        String parentId,
        Set<CategoryCreatedEvent> subCategories,
        int level,
        Instant createdAt,
        Instant updatedAt,
        String aggregateName,
        String eventType,
        Instant occurredOn
) implements DomainEvent {

    private CategoryCreatedEvent(
            final String id,
            final String name,
            final String description,
            final String slug,
            final String parentId,
            final Set<CategoryCreatedEvent> subCategories,
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
                EventsTypes.CATEGORY_CREATED,
                InstantUtils.now()
        );
    }

    public static CategoryCreatedEvent from(final Category aCategory) {
        return new CategoryCreatedEvent(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getSubCategories().stream()
                        .map(CategoryCreatedEvent::from).collect(Collectors.toSet()),
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
                subCategories.stream().map(CategoryCreatedEvent::toDomain).collect(Collectors.toSet()),
                level(),
                createdAt(),
                updatedAt(),
                null
        );
    }
}
