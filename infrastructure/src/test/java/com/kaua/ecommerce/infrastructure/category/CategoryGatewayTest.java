package com.kaua.ecommerce.infrastructure.category;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseGatewayTest
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
        final CategoryID aParent = null;

        final var aCategory = Category.newCategory(aName, aDescription, aSlug, aParent);

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategory.getParentId(), actualCategory.getParentId());
        Assertions.assertEquals(aCategory.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());

        final var actualEntity = categoryRepository.findById(actualCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCategory.getName(), actualEntity.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualEntity.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualEntity.getSlug());
        Assertions.assertNull(actualEntity.getParentId());
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
        final var aCategory = Fixture.Categories.tech();
        final var aName = aCategory.getName();

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertTrue(categoryGateway.existsByName(aName));
    }

    @Test
    void givenAValidSubCategoryWithDescription_whenCallUpdate_shouldReturnACategoryRootUpdated() {
        final var aCategoryRoot = Fixture.Categories.home();
        categoryRepository.save(CategoryJpaEntity.toEntity(aCategoryRoot));

        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";
        final var aSlug = "sub-category-test";

        final var aSubCategory = Category.newCategory(aName, aDescription, aSlug, aCategoryRoot.getId());
        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.update(aCategoryRoot);

        Assertions.assertEquals(2, categoryRepository.count());

        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategoryRoot.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategoryRoot.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategoryRoot.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategoryRoot.getParentId(), actualCategory.getParentId());
        Assertions.assertEquals(aCategoryRoot.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(1, actualCategory.getSubCategories().size());
        Assertions.assertEquals(aCategoryRoot.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategoryRoot.getUpdatedAt(), actualCategory.getUpdatedAt());

        final var actualEntity = categoryRepository.findById(actualCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(aCategoryRoot.getName(), actualEntity.getName());
        Assertions.assertEquals(aCategoryRoot.getDescription(), actualEntity.getDescription());
        Assertions.assertEquals(aCategoryRoot.getSlug(), actualEntity.getSlug());
        Assertions.assertNull(actualEntity.getParentId());
        Assertions.assertEquals(1, actualEntity.getSubCategories().size());
        Assertions.assertEquals(aCategoryRoot.getLevel(), actualEntity.getSubCategoriesLevel());
        Assertions.assertEquals(aCategoryRoot.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategoryRoot.getUpdatedAt(), actualEntity.getUpdatedAt());
    }

    @Test
    void givenAValidCategoryWithSubCategories_whenCallFindById_shouldReturnACategory() {
        final var aCategoryRoot = Category.newCategory("Root", null, "root", null);
        final var aSubCategory = Category.newCategory("Sub", null, "sub", aCategoryRoot.getId());
        aCategoryRoot.addSubCategory(aSubCategory);
        aCategoryRoot.updateSubCategoriesLevel();
        categoryRepository.save(CategoryJpaEntity.toEntity(aCategoryRoot));

        Assertions.assertEquals(2, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(aCategoryRoot.getId().getValue()).get();

        Assertions.assertEquals(aCategoryRoot.getId().getValue(), actualCategory.getId().getValue());
        Assertions.assertEquals(aCategoryRoot.getName(), actualCategory.getName());
        Assertions.assertEquals(aCategoryRoot.getDescription(), actualCategory.getDescription());
        Assertions.assertEquals(aCategoryRoot.getSlug(), actualCategory.getSlug());
        Assertions.assertEquals(aCategoryRoot.getParentId(), actualCategory.getParentId());
        Assertions.assertEquals(aCategoryRoot.getLevel(), actualCategory.getLevel());
        Assertions.assertEquals(1, actualCategory.getSubCategories().size());
        Assertions.assertEquals(aCategoryRoot.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategoryRoot.getUpdatedAt(), actualCategory.getUpdatedAt());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallFindById_shouldReturnEmpty() {
        final var aId = "123";
        final var actualCategory = categoryGateway.findById(aId);
        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    void givenAValidCategoryId_whenCallDeleteById_shouldBeOk() {
        final var aCategory = Fixture.Categories.tech();

        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));
        Assertions.assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId().getValue());

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteById_shouldBeOk() {
        final var aCategoryId = "123";

        Assertions.assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(aCategoryId);

        Assertions.assertEquals(0, categoryRepository.count());
    }
}
