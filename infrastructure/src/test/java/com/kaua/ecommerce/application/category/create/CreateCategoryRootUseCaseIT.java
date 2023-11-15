package com.kaua.ecommerce.application.category.create;

import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootCommand;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.utils.SlugUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntity;
import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CreateCategoryRootUseCaseIT {

    @Autowired
    private CreateCategoryRootUseCase createCategoryRootUseCase;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Test
    void givenAValidValuesWithDescription_whenCallsCreateCategoryRootUseCase_thenCategoryShouldBeCreated() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";
        final var aExpectedSlug = SlugUtils.createSlug(aName);

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategory = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertEquals(aDescription, aCategory.getDescription());
        Assertions.assertEquals(aExpectedSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
        Assertions.assertEquals(0, aCategory.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAValidValuesWithoutDescription_whenCallsCreateCategoryRootUseCase_thenCategoryShouldBeCreated() {
        final var aName = "Category Test";
        final String aDescription = null;
        final var aExpectedSlug = SlugUtils.createSlug(aName);

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult);
        Assertions.assertNotNull(aResult.id());

        final var aCategory = this.categoryRepository.findById(aResult.id()).get();

        Assertions.assertNotNull(aCategory.getId());
        Assertions.assertEquals(aName, aCategory.getName());
        Assertions.assertNull(aCategory.getDescription());
        Assertions.assertEquals(aExpectedSlug, aCategory.getSlug());
        Assertions.assertTrue(aCategory.getParent().isEmpty());
        Assertions.assertEquals(0, aCategory.getSubCategoriesLevel());
        Assertions.assertEquals(0, aCategory.getSubCategories().size());
        Assertions.assertNotNull(aCategory.getCreatedAt());
        Assertions.assertNotNull(aCategory.getUpdatedAt());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameExists_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        this.categoryRepository.save(
                CategoryJpaEntity.toEntity(Category.newCategory(
                        aName,
                        "Category Test Description",
                        SlugUtils.createSlug(aName),
                        null
                )));

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNullNameAndNullSlug_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3AndSlugLengthLessThan3_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255AndSlugLengthMoreThan255_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aResult.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aResult.getErrors().get(1).message());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallsCreateCategoryRootUseCase_shouldReturnDomainException() {
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        final var aResult = this.createCategoryRootUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.categoryRepository.count());
    }
}
