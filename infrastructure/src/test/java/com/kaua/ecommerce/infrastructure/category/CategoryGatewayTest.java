package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CategoryGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Test
    void givenAValidCategoryWithDescription_whenCallCreate_shouldReturnACategoryCreated() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";
        final var aSlug = "category-test";
        final Category aParent = null;

        final var aCategory = Category.newCategory(aName, aDescription, aSlug, aParent);

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategory.getParent(), actualCategory.getParent());
        Assertions.assertEquals(aCategory.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());

        final var actualEntity = categoryRepository.findById(actualCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCategory.getName(), actualEntity.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualEntity.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualEntity.getSlug());
        Assertions.assertTrue(actualEntity.getParent().isEmpty());
        Assertions.assertEquals(aCategory.getLevel(), actualEntity.getSubCategoriesLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidNameButNotExists_whenCallExistsByName_shouldReturnFalse() {
        final var aName = "Category Test";

        Assertions.assertEquals(0, categoryRepository.count());
        Assertions.assertFalse(categoryGateway.existsByName(aName));
    }

    @Test
    void givenAValidName_whenCallExistsByName_shouldReturnTrue() {
        final var aCategory = Fixture.Categories.categoryDefaultRoot;
        final var aName = aCategory.getName();

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertTrue(categoryGateway.existsByName(aName));
    }
}
