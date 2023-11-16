package com.kaua.ecommerce.infrastructure.category.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {

    @Id
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @JsonIgnore
    @Column(name = "parent_id")
    private String parentId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "categories_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_category_id")
    )
    private Set<CategoryJpaEntity> subCategories;

    @Column(name = "sub_categories_level", nullable = false)
    private int subCategoriesLevel;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public CategoryJpaEntity() {}

    private CategoryJpaEntity(
            final String id,
            final String name,
            final String description,
            final String slug,
            final String parentId,
            final int subCategoriesLevel,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.parentId = parentId;
        this.subCategories = new HashSet<>();
        this.subCategoriesLevel = subCategoriesLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CategoryJpaEntity toEntity(final Category aCategory) {
        final var aEntity = new CategoryJpaEntity(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getLevel(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );


        aCategory.getSubCategories().forEach(aEntity::addCategory);

        return aEntity;
    }

    public Category toDomain() {
        return Category.with(
                getId(),
                getName(),
                getDescription(),
                getSlug(),
                getParentId(),
                getSubCategories().stream().map(CategoryJpaEntity::toDomain).collect(Collectors.toSet()),
                getSubCategoriesLevel(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

    public void addCategory(final Category aCategory) {
        this.subCategories.add(CategoryJpaEntity.toEntity(aCategory));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Set<CategoryJpaEntity> getSubCategories() {
        return subCategories;
    }

    public int getSubCategoriesLevel() {
        return subCategoriesLevel;
    }

    public void setSubCategoriesLevel(int subCategoriesLevel) {
        this.subCategoriesLevel = subCategoriesLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}