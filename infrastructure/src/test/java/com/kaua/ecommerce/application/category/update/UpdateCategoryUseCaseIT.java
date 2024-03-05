package com.kaua.ecommerce.application.category.update;

import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Autowired
    private CategoryJpaEntityRepository categoryRepository;

    @Test
    void givenAValidValuesWithDescription_whenCallsUpdateCategoryUseCase_thenCategoryShouldBeUpdated() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aName, aCategoryWithSub.getName());
        Assertions.assertEquals(aDescription, aCategoryWithSub.getDescription());
        Assertions.assertNotNull(aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertTrue(aCategoryUpdatedAt.isBefore(aCategoryWithSub.getUpdatedAt()));
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallsUpdateCategoryUseCase_thenCategoryShouldBeUpdated() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final String aDescription = null;

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aName, aCategoryWithSub.getName());
        Assertions.assertNull(aCategoryWithSub.getDescription());
        Assertions.assertNotNull(aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertTrue(aCategoryUpdatedAt.isBefore(aCategoryWithSub.getUpdatedAt()));
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameExists_whenCallsUpdateCategoryUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Tech";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidRootId_whenCallsUpdateCategoryUseCase_shouldThrowNotFoundException() {
        final var aId = "123";
        final var aName = "Category Name Two";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateCategoryUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNullNameAndNullSlug_whenCallsUpdateCategoryUseCase_shouldReturnOldNameAndOldSlugCategory() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aCategory.getName(), aCategoryWithSub.getName());
        Assertions.assertEquals(aDescription, aCategoryWithSub.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertTrue(aCategoryUpdatedAt.isBefore(aCategoryWithSub.getUpdatedAt()));
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallsUpdateCategoryUseCase_shouldReturnOldNameAndOldSlugCategory() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aCategory.getName(), aCategoryWithSub.getName());
        Assertions.assertEquals(aDescription, aCategoryWithSub.getDescription());
        Assertions.assertEquals(aCategory.getSlug(), aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertTrue(aCategoryUpdatedAt.isBefore(aCategoryWithSub.getUpdatedAt()));
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3AndSlugLengthLessThan3_whenCallsUpdateCategoryUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255AndSlugLengthMoreThan255_whenCallsUpdateCategoryUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan1000_whenCallsUpdateCategoryUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.home();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(1001);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 1000);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateCategoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }
}
