package com.kaua.ecommerce.infrastructure.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Node("categories")
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @Column(name = "category_id")
    private String id;

    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private CategoryEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CategoryEntity> subcategories;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    public CategoryEntity() {
    }

    public CategoryEntity(CategoryEntity parent) {
        this.parent = parent;
    }

    public CategoryEntity(String id, String name, String description, CategoryEntity parent, List<CategoryEntity> subcategories, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.subcategories = subcategories;
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

    public CategoryEntity getParent() {
        return parent;
    }

    public void setParent(CategoryEntity parent) {
        this.parent = parent;
    }

    public List<CategoryEntity> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryEntity> subcategories) {
        this.subcategories = subcategories;
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

    //
//    @Property(name = "name")
//    private String name;
//
//    @Property(name = "description")
//    private String description;
//
//    @Property(name = "is_primary")
//    private boolean isPrimary;
//
//    @Relationship(type = "HAS_SUBCATEGORY", direction = Relationship.Direction.OUTGOING)
//    private Set<SubCategoryEntity> subCategoriesIds;
//
//    @Property(name = "created_at")
//    private Instant createdAt;
//
//    @Property(name = "updated_at")
//    private Instant updatedAt;
//
//    public CategoryEntity() {
//    }
//
//    public CategoryEntity(String id, String name, String description, boolean isPrimary, Set<SubCategoryEntity> subCategoriesIds, Instant createdAt, Instant updatedAt) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.isPrimary = isPrimary;
//        this.subCategoriesIds = subCategoriesIds == null ? new HashSet<>() : subCategoriesIds;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public boolean isPrimary() {
//        return isPrimary;
//    }
//
//    public void setPrimary(boolean primary) {
//        isPrimary = primary;
//    }
//
//    public Set<SubCategoryEntity> getSubCategoriesIds() {
//        return subCategoriesIds;
//    }
//
//    public void setSubCategories(Set<SubCategoryEntity> subCategories) {
//        this.subCategoriesIds = subCategories;
//    }
//
//    public Instant getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Instant createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Instant getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Instant updatedAt) {
//        this.updatedAt = updatedAt;
//    }
}
