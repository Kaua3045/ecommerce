package com.kaua.ecommerce.infrastructure.category.persistence;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Document(indexName = "categories")
public class CategoryElasticsearchEntity {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "name"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String name;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Text, name = "slug")
    private String slug;

    @Field(type = FieldType.Text, name = "parent_id")
    private String parentId;

    @Field(type = FieldType.Nested, ignoreFields = {"subCategories"})
    private Set<CategoryElasticsearchEntity> subCategories;

    @Field(type = FieldType.Integer, name = "sub_categories_level")
    private int subCategoriesLevel;

    @Field(type = FieldType.Date, name = "created_at")
    private Instant createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
    private Instant updatedAt;

    public CategoryElasticsearchEntity() {}

    private CategoryElasticsearchEntity(
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

    public static CategoryElasticsearchEntity toEntity(final Category aCategory) {
        final var aEntity = new CategoryElasticsearchEntity(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getSlug(),
                aCategory.getParentId().map(CategoryID::getValue).orElse(null),
                aCategory.getLevel(),
                aCategory.getCreatedAt(),
                aCategory.getUpdatedAt()
        );

        aCategory.getSubCategories().forEach(aEntity::addSubCategory);

        return aEntity;
    }

    public Category toDomain() {
        return Category.with(
                getId(),
                getName(),
                getDescription(),
                getSlug(),
                getParentId(),
                getSubCategories().stream().map(CategoryElasticsearchEntity::toDomain).collect(Collectors.toSet()),
                getSubCategoriesLevel(),
                getCreatedAt(),
                getUpdatedAt(),
                null
        );
    }

    public void addSubCategory(final Category aCategory) {
        this.subCategories.add(CategoryElasticsearchEntity.toEntity(aCategory));
    }

    public String getId() {
        return id;
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

    public String getParentId() {
        return parentId;
    }

    public Set<CategoryElasticsearchEntity> getSubCategories() {
        return subCategories;
    }

    public int getSubCategoriesLevel() {
        return subCategoriesLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
