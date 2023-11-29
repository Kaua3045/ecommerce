package com.kaua.ecommerce.application.category.update.subcategories;

import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesCommand;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class UpdateSubCategoriesUseCaseIT {

    @Autowired
    private UpdateSubCategoriesUseCase updateSubCategoriesUseCase;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Test
    void givenAValidValuesWithDescription_whenCallsUpdateSubCategoriesUseCase_thenCategoryShouldBeUpdated() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aCategory.getName(), aCategoryWithSub.getName());
        Assertions.assertEquals(aCategory.getDescription(), aCategoryWithSub.getDescription());
        Assertions.assertNotNull(aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(1, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(1, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertNotNull(aCategoryWithSub.getUpdatedAt());
        Assertions.assertEquals(2, this.categoryRepository.count());
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallsUpdateSubCategoriesUseCase_thenCategoryShouldBeUpdated() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Sub Category Test";
        final String aDescription = null;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategoryWithSub = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertEquals(aId, aCategoryWithSub.getId());
        Assertions.assertEquals(aCategory.getName(), aCategoryWithSub.getName());
        Assertions.assertEquals(aCategory.getDescription(), aCategoryWithSub.getDescription());
        Assertions.assertNotNull(aCategoryWithSub.getSlug());
        Assertions.assertNull(aCategoryWithSub.getParentId());
        Assertions.assertEquals(1, aCategoryWithSub.getSubCategoriesLevel());
        Assertions.assertEquals(1, aCategoryWithSub.getSubCategories().size());
        Assertions.assertNotNull(aCategoryWithSub.getCreatedAt());
        Assertions.assertNotNull(aCategoryWithSub.getUpdatedAt());
        Assertions.assertEquals(2, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameExists_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Tech";
        final var aDescription = "Sub Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidRootCategoryId_whenCallsUpdateSubCategoriesUseCase_shouldThrowNotFoundException() {
        final var aId = "123";
        final var aName = "Category Name Two";
        final var aDescription = "Sub Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateSubCategoriesUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNullNameAndNullSlug_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3AndSlugLengthLessThan3_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255AndSlugLengthMoreThan255_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallsUpdateSubCategoriesUseCase_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.home();
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = this.updateSubCategoriesUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidSubCategoriesLengthMoreThan5_whenCallsUpdateSubCategoriesUseCase_shouldThrowDomainException() {
        final var aCategory = Fixture.Categories.home();
        aCategory.addSubCategories(Fixture.Categories.makeSubCategories(5, aCategory));
        this.categoryRepository.save(CategoryJpaEntity.toEntity(aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        final var aResult = Assertions.assertThrows(DomainException.class,
                () -> this.updateSubCategoriesUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(6, this.categoryRepository.count());
    }
}
