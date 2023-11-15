package com.kaua.ecommerce.application.usecases.category.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateCategoryRootUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultCreateCategoryRootUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    void givenAValidCommandWithDescription_whenCallCreateCategory_shouldReturnACategoryIdCreated() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";
        final var aSlug = "category-test";

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).create(argThat(aCategory ->
                Objects.nonNull(aCategory.getId()) &&
                Objects.equals(aName, aCategory.getName()) &&
                Objects.equals(aDescription, aCategory.getDescription()) &&
                Objects.equals(aSlug, aCategory.getSlug()) &&
                aCategory.getParent().isEmpty() &&
                Objects.equals(0, aCategory.getSubCategories().size()) &&
                Objects.nonNull(aCategory.getCreatedAt()) &&
                Objects.nonNull(aCategory.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandWithoutDescription_whenCallCreateCategory_shouldReturnACategoryIdCreated() {
        final var aName = "Category Test";
        final String aDescription = null;
        final var aSlug = "category-test";

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);
        Mockito.when(categoryGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = useCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.id());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(1)).create(argThat(aCategory ->
                Objects.nonNull(aCategory.getId()) &&
                        Objects.equals(aName, aCategory.getName()) &&
                        Objects.isNull(aCategory.getDescription()) &&
                        Objects.equals(aSlug, aCategory.getSlug()) &&
                        aCategory.getParent().isEmpty() &&
                        Objects.equals(0, aCategory.getSubCategories().size()) &&
                        Objects.nonNull(aCategory.getCreatedAt()) &&
                        Objects.nonNull(aCategory.getUpdatedAt())));
    }

    @Test
    void givenAnExistsName_whenCallCreateCategory_shouldReturnAnDomainException() {
        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(true);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullNameAndNullSlug_whenCallCreateCategory_shouldReturnAnDomainException() {
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankNameAndResultInBlankSlug_whenCallCreateCategory_shouldReturnAnDomainException() {
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.nullOrBlank("name");
        final var expectedErrorMessageTwo = CommonErrorMessage.nullOrBlank("slug");
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3AndResultInSlugLengthInvalid_whenCallCreateCategory_shouldReturnAnDomainException() {
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNameLengthMoreThan255AndResultInSlugLengthInvalid_whenCallCreateCategory_shouldReturnAnDomainException() {
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessageOne = CommonErrorMessage.lengthBetween("name", 3, 255);
        final var expectedErrorMessageTwo = CommonErrorMessage.lengthBetween("slug", 3, 255);
        final var expectedErrorCount = 2;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, aOutput.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, aOutput.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidDescriptionLengthMoreThan255_whenCallCreateCategory_shouldReturnAnDomainException() {
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryRootCommand.with(aName, aDescription);

        Mockito.when(categoryGateway.existsByName(aName)).thenReturn(false);

        final var aOutput = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByName(aName);
        Mockito.verify(categoryGateway, Mockito.times(0)).create(Mockito.any());
    }
}
