package com.kaua.ecommerce.application.usecases.category.update;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class UpdateCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Test
    void givenAValidCommandWithDescription_whenCallUpdateCategory_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(aName, aCategoryUpdated.getName()) &&
                        Objects.equals(aDescription, aCategoryUpdated.getDescription()) &&
                        Objects.nonNull(aCategoryUpdated.getSlug()) &&
                        aCategoryUpdated.getParentId().isEmpty() &&
                        Objects.equals(0, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(0, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getCreatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAValidCommandWithoutDescription_whenCallUpdateCategory_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final String aDescription = null;

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(aName, aCategoryUpdated.getName()) &&
                        Objects.isNull(aCategoryUpdated.getDescription()) &&
                        Objects.nonNull(aCategoryUpdated.getSlug()) &&
                        aCategoryUpdated.getParentId().isEmpty() &&
                        Objects.equals(0, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(0, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getCreatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAValidCommandWithSubCategoryId_whenCallUpdateCategory_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();
        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";

        final var aCommand = UpdateCategoryCommand.with(aId, aSubId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(aCategory.getName(), aCategoryUpdated.getName()) &&
                        Objects.equals(aCategory.getDescription(), aCategoryUpdated.getDescription()) &&
                        Objects.nonNull(aCategoryUpdated.getSlug()) &&
                        aCategoryUpdated.getParentId().isEmpty() &&
                        Objects.equals(1, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(aName, aCategoryUpdated.getSubCategories()
                                .stream().findFirst().get().getName()) &&
                        Objects.equals(1, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getCreatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        Objects.equals(aCategory.getUpdatedAt(), aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAValidCommandWithSubSubCategoryId_whenCallUpdateCategory_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        final var aSubSubCategory = Category.newCategory(
                "Sub Sub Category Test",
                "Sub Sub Category Test Description",
                "sub-sub-category-test",
                aSubCategory.getId());

        aSubCategory.addSubCategory(aSubSubCategory);
        aSubCategory.updateSubCategoriesLevel();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubSubCategory.getId().getValue();
        final var aName = "Sub Sub Category";
        final String aDescription = null;

        final var aCommand = UpdateCategoryCommand.with(aId, aSubId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        System.out.println(aCategory.getSubCategories().stream().findFirst().get().getSubCategories().stream().findFirst().get().getName());
        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(1, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(aName, aCategoryUpdated.getSubCategories()
                                .stream().findFirst().get().getSubCategories()
                                .stream().findFirst().get().getName()) &&
                        Objects.equals(1, aCategoryUpdated.getLevel()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAnExistsName_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with("123", null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(true);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallUpdateCategory_shouldThrowNotFoundException() {
        final var aId = "123";
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenANullNameAndSlug_whenCallUpdateCategory_shouldReturnOldNameAndOldSlug() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(aCategory.getName(), aCategoryUpdated.getName()) &&
                        Objects.equals(aDescription, aCategoryUpdated.getDescription()) &&
                        Objects.equals(aCategory.getSlug(), aCategoryUpdated.getSlug()) &&
                        aCategoryUpdated.getParentId().isEmpty() &&
                        Objects.equals(0, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(0, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getCreatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(aCategory.getName(), aCategoryUpdated.getName()) &&
                        Objects.equals(aDescription, aCategoryUpdated.getDescription()) &&
                        Objects.equals(aCategory.getSlug(), aCategoryUpdated.getSlug()) &&
                        aCategoryUpdated.getParentId().isEmpty() &&
                        Objects.equals(0, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(0, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getCreatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAnInvalidNameLengthLessThan3AndResultInSlugLengthInvalid_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255AndResultInSlugLengthInvalid_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAValidCommandWithInvalidNotExistsSubSubCategoryId_whenCallUpdateCategory_shouldReturnACategoryId() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = "123";
        final var aName = "Sub Sub Category";
        final String aDescription = null;

        final var aCommand = UpdateCategoryCommand.with(aId, aSubId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(1)).update(argThat(aCategoryUpdated ->
                Objects.equals(aId, aCategoryUpdated.getId().getValue()) &&
                        Objects.equals(1, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(1, aCategoryUpdated.getLevel()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAnInvalidCommandWithValidSubCategoryId_whenCallUpdateCategory_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Sub Sub Category Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, aSubId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCommandWithValidSubSubCategoryId_whenCallUpdateCategory_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory)
                .stream().findFirst().get();
        final var aSubSubCategory = Category.newCategory(
                "Sub Sub Category Test",
                "Sub Sub Category Test Description",
                "sub-sub-category-test",
                aSubCategory.getId());

        aSubCategory.addSubCategory(aSubSubCategory);
        aSubCategory.updateSubCategoriesLevel();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubSubCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Sub Sub Category Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateCategoryCommand.with(aId, aSubId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }
}
