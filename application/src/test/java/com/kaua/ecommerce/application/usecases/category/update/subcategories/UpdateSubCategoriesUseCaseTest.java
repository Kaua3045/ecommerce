package com.kaua.ecommerce.application.usecases.category.update.subcategories;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
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

public class UpdateSubCategoriesUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateSubCategoriesUseCase useCase;

    @Test
    void givenAValidCommandWithDescription_whenCallUpdateSubCategories_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
                        Objects.equals(aCategory.getSlug(), aCategoryUpdated.getSlug()) &&
                        aCategory.getParentId().isEmpty() &&
                        Objects.equals(aName, aCategoryUpdated.getSubCategories().stream()
                                .findFirst().get().getName()) &&
                        Objects.equals(1, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(1, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getUpdatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        Objects.equals(aCategory.getUpdatedAt(), aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAValidCommandWithDescriptionWitSubCategoryId_whenCallUpdateSubCategories_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aName = "Sub Sub Category Test";
        final var aDescription = "Sub Sub Category Test Description";

        final var aCommand = UpdateSubCategoriesCommand.with(aId, aSubCategory.getId().getValue(), aName, aDescription);

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
                        Objects.equals(aCategory.getSlug(), aCategoryUpdated.getSlug()) &&
                        aCategory.getParentId().isEmpty() &&
                        Objects.equals(1, aCategoryUpdated.getSubCategories().size()) &&
                        Objects.equals(1, aCategoryUpdated.getLevel()) &&
                        Objects.equals(aCategory.getUpdatedAt(), aCategoryUpdated.getCreatedAt()) &&
                        Objects.equals(aCategory.getUpdatedAt(), aCategoryUpdated.getUpdatedAt()) &&
                        Objects.equals(1, aCategoryUpdated.getDomainEvents().size())));
    }

    @Test
    void givenAnExistsName_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with("123", null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(true);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidRootCategoryId_whenCallUpdateSubCategories_shouldThrowNotFoundException() {
        final var aId = "123";
        final var aName = "Sub Category Test";
        final var aDescription = "Sub Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
    void givenAnInvalidNullNameAndNullSlug_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        System.out.println(aOutput.getErrors().get(0).message());

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
    void givenAnInvalidNameLengthLessThan3AndResultInSlugLengthInvalid_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
    void givenAnInvalidNameLengthMoreThan255AndResultInSlugLengthInvalid_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallUpdateSubCategories_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

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
    void givenAnInvalidSubCategoriesLengthMoreThan5_whenCallUpdateSubCategories_shouldThrowDomainException() {
        final var aCategory = Fixture.Categories.tech();
        aCategory.addSubCategories(Fixture.Categories.makeSubCategories(5, aCategory));

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);
        final var expectedErrorCount = 1;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, null, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = Assertions.assertThrows(DomainException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCommandWitSubCategoryId_whenCallUpdateSubCategories_shouldReturnDomainException() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Sub Sub Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = UpdateSubCategoriesCommand.with(aId, aSubCategory.getId().getValue(), aName, aDescription);

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
    void givenAValidCommandWitInvalidSubCategoryId_whenCallUpdateSubCategories_shouldThrowNotFoundException() {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aSubCategoryInvalidId = "123";

        final var aId = aCategory.getId().getValue();
        final var aName = "Sub Sub Category Test";
        final var aDescription = "Sub Sub Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aSubCategoryInvalidId);

        final var aCommand = UpdateSubCategoriesCommand.with(aId, aSubCategoryInvalidId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }
}
