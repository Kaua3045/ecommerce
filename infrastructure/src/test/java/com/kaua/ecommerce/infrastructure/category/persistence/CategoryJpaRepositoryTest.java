package com.kaua.ecommerce.infrastructure.category.persistence;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.Set;

@DatabaseGatewayTest
public class CategoryJpaRepositoryTest {

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Test
    void givenAnInvalidNullName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity.name";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidNullDescription_whenCallSave_shouldReturnACategory() {
        final var aCategory = Fixture.Categories.home();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setDescription(null);

        final var actualOutput = Assertions.assertDoesNotThrow(() -> categoryRepository.save(aEntity))
                .toDomain();

        Assertions.assertEquals(aEntity.getId(), actualOutput.getId().getValue());
        Assertions.assertEquals(aEntity.getName(), actualOutput.getName());
        Assertions.assertNull(actualOutput.getDescription());
        Assertions.assertEquals(aEntity.getSlug(), actualOutput.getSlug());
        Assertions.assertTrue(actualOutput.getParentId().isEmpty());
        Assertions.assertEquals(aEntity.getSubCategoriesLevel(), actualOutput.getLevel());
        Assertions.assertEquals(0, aEntity.getSubCategories().size());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualOutput.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualOutput.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullSlug_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "slug";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity.slug";

        final var aCategory = Fixture.Categories.home();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setSlug(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidParentId_whenCallSave_shouldReturnACategory() {
        final var aCategory = Fixture.Categories.tech();
        categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aSubCategory = Category.newCategory(
                "Sub Category Test",
                "Sub Category Test Description",
                "sub-category-test",
                null
        );

        final var aEntity = CategoryJpaEntity.toEntity(aSubCategory);
        aEntity.setParentId(aCategory.getId().getValue());
        aCategory.addSubCategories(Set.of(aSubCategory));
        aCategory.updateSubCategoriesLevel();

        final var actualOutput = Assertions.assertDoesNotThrow(() ->
                categoryRepository.save(CategoryJpaEntity.toEntity(aCategory))).toDomain();

        Assertions.assertEquals(aCategory.getId().getValue(), actualOutput.getId().getValue());
        Assertions.assertEquals(aCategory.getName(), actualOutput.getName());
        Assertions.assertEquals(aCategory.getDescription(), actualOutput.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), actualOutput.getSlug());
        Assertions.assertTrue(actualOutput.getParentId().isEmpty());
        Assertions.assertEquals(aCategory.getLevel(), actualOutput.getLevel());
        Assertions.assertEquals(1, actualOutput.getSubCategories().size());
        Assertions.assertTrue(actualOutput.getSubCategories().contains(aSubCategory));
        Assertions.assertEquals(aCategory.getCreatedAt(), actualOutput.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualOutput.getUpdatedAt());
    }

    @Test
    void givenAValidLevelLength_whenCallSave_shouldReturnAnException() {
        final var aCategory = Fixture.Categories.tech();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setSubCategoriesLevel(5);

        final var actualOutput = Assertions.assertDoesNotThrow(() -> categoryRepository.save(aEntity))
                .toDomain();

        Assertions.assertEquals(aEntity.getId(), actualOutput.getId().getValue());
        Assertions.assertEquals(aEntity.getName(), actualOutput.getName());
        Assertions.assertEquals(aEntity.getDescription(), actualOutput.getDescription());
        Assertions.assertEquals(aEntity.getSlug(), actualOutput.getSlug());
        Assertions.assertTrue(actualOutput.getParentId().isEmpty());
        Assertions.assertEquals(aEntity.getSubCategoriesLevel(), actualOutput.getLevel());
        Assertions.assertEquals(0, aEntity.getSubCategories().size());
        Assertions.assertEquals(aEntity.getCreatedAt(), actualOutput.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), actualOutput.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

        final var aCategory = Fixture.Categories.home();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

        final var aCategory = Fixture.Categories.tech();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> categoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "ids for this class must be manually assigned before calling save(): com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity";

        final var aCategory = Fixture.Categories.home();

        final var aEntity = CategoryJpaEntity.toEntity(aCategory);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> categoryRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
