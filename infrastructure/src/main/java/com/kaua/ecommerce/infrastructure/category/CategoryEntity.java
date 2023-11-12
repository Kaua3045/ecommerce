package com.kaua.ecommerce.infrastructure.category;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Node("categories")
public class CategoryEntity {

    @Id
    private String id;

    @Property(name = "name")
    private String name;

    @Property(name = "description")
    private String description;

    @Property(name = "is_primary")
    private boolean isPrimary;

    @Relationship(type = "HAS_SUBCATEGORY", direction = Relationship.Direction.OUTGOING)
    private Set<CategoryEntity> subCategories;

    @Property(name = "created_at")
    private Instant createdAt;

    @Property(name = "updated_at")
    private Instant updatedAt;

    public CategoryEntity() {
    }

    public CategoryEntity(String id, String name, String description, boolean isPrimary, Set<CategoryEntity> subCategories, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPrimary = isPrimary;
        this.subCategories = subCategories == null ? new HashSet<>() : subCategories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Set<CategoryEntity> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<CategoryEntity> subCategories) {
        this.subCategories = subCategories;
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
