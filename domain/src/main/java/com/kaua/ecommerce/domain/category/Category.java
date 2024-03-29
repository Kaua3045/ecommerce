package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.AggregateRoot;
import com.kaua.ecommerce.domain.event.DomainEvent;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.*;

public class Category extends AggregateRoot<CategoryID> {

    private String name;
    private String description;
    private String slug;
    private CategoryID parentId;
    private Set<Category> subCategories;
    private int level;
    private Instant createdAt;
    private Instant updatedAt;

    private Category(
            final CategoryID aCategoryID,
            final String aName,
            final String aDescription,
            final String aSlug,
            final CategoryID aParentId,
            final Set<Category> aSubCategories,
            final int aLevel,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final List<DomainEvent> aDomainEvents
    ) {
        super(aCategoryID, aDomainEvents);
        this.name = aName;
        this.description = aDescription;
        this.slug = aSlug;
        this.parentId = aParentId;
        this.subCategories = aSubCategories;
        this.level = aLevel;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
    }

    public static Category newCategory(
            final String aName,
            final String aDescription,
            final String aSlug,
            final CategoryID aParentId
    ) {
        final var aId = CategoryID.unique();
        final var aNow = InstantUtils.now();
        return new Category(
                aId,
                aName,
                aDescription,
                aSlug,
                aParentId,
                new HashSet<>(),
                0,
                aNow,
                aNow,
                null
        );
    }

    public static Category with(
            final String aId,
            final String aName,
            final String aDescription,
            final String aSlug,
            final String aParentId,
            final Set<Category> aSubCategories,
            final int aLevel,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final List<DomainEvent> aDomainEvents
    ) {
        return new Category(
                CategoryID.from(aId),
                aName,
                aDescription,
                aSlug,
                aParentId == null ? null : CategoryID.from(aParentId),
                new HashSet<>(aSubCategories == null ? Collections.emptySet() : aSubCategories),
                aLevel,
                aCreatedAt,
                aUpdatedAt,
                aDomainEvents
        );
    }

    public Category update(final String aName, final String aDescription, final String aSlug) {
        this.name = aName;
        this.description = aDescription;
        this.slug = aSlug;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public void updateSubCategoriesLevel() {
        if (this.level == 5 || this.level + this.subCategories.size() > 5) {
            throw DomainException.with(new Error(CommonErrorMessage.lengthBetween("subCategories", 0, 5)));
        }
        this.level = this.subCategories.size();
    }

    public void updateRemoveSubCategoriesLevel() {
        if (this.level != 0) {
            this.level = this.level - 1;
        }
    }

    public void addSubCategory(final Category aSubCategory) {
        this.subCategories.add(aSubCategory);
    }

    public void addSubCategories(final Set<Category> aSubCategory) {
        this.subCategories.addAll(aSubCategory);
    }

    public void removeSubCategory(final Category aSubCategory) {
        this.subCategories.remove(aSubCategory);
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

    public Optional<CategoryID> getParentId() {
        return Optional.ofNullable(parentId);
    }

    public Set<Category> getSubCategories() {
        return subCategories;
    }

    public int getLevel() {
        return level;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Category(" +
                "id='" + getId().getValue() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", slug='" + slug + '\'' +
                ", parentId=" + getParentId().map(CategoryID::getValue).orElse(null) +
                ", subCategories=" + subCategories.size() +
                ", level=" + level +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ')';
    }
}
