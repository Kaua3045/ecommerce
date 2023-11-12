package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Category extends AggregateRoot<CategoryID> {

    private String name;
    private String description;
    private String slug;
    private boolean isRoot;
    private Set<Category> subCategories;
    private Instant createdAt;
    private Instant updatedAt;

    private Category(
            final CategoryID aCategoryID,
            final String aName,
            final String aDescription,
            final String aSlug,
            final boolean aIsRoot,
            final Set<Category> aSubCategories,
            final Instant aCreatedAt,
            final Instant aUpdatedAt
    ) {
        super(aCategoryID);
        this.name = aName;
        this.description = aDescription;
        this.slug = aSlug;
        this.isRoot = aIsRoot;
        this.subCategories = aSubCategories;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Category newCategory(
            final String aName,
            final String aDescription,
            final String aSlug,
            final boolean aIsRoot
    ) {
        final var aId = CategoryID.unique();
        final var aNow = InstantUtils.now();
        return new Category(
                aId,
                aName,
                aDescription,
                aSlug,
                aIsRoot,
                new HashSet<>(),
                aNow,
                aNow
        );
    }

    public void addSubCategories(final Set<Category> aSubCategory) {
        this.subCategories.addAll(aSubCategory);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new CategoryValidation(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Set<Category> getSubCategories() {
        return Collections.unmodifiableSet(subCategories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
