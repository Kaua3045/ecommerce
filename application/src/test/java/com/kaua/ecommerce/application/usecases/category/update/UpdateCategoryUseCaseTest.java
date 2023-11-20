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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandWithoutDescription_whenCallUpdateCategory_shouldReturnACategoryIdUpdated() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = "Category Test";
        final String aDescription = null;

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt())));
    }

    @Test
    void givenAnExistsName_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with("123", aName, aDescription);

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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidBlankNameAndBlankSlug_whenCallUpdateCategory_shouldReturnAnDomainException() {
        final var aCategory = Fixture.Categories.tech();

        final var aId = aCategory.getId().getValue();
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var aCategoryUpdatedAt = aCategory.getUpdatedAt();

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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
                        aCategoryUpdatedAt.isBefore(aCategoryUpdated.getUpdatedAt())));
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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

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

        final var aCommand = UpdateCategoryCommand.with(aId, aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.findById(aId)).thenReturn(Optional.of(aCategory));

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(categoryGateway, Mockito.times(0)).update(Mockito.any());
    }
}
