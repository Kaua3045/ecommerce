package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootCommand;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootOutput;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.get.GetCategoryByIdOutput;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.ListCategoriesOutput;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.ListCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryOutput;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesCommand;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesOutput;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.category.models.CreateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.UpdateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.UpdateSubCategoriesInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.argThat;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryRootUseCase createCategoryRootUseCase;

    @MockBean
    private UpdateSubCategoriesUseCase updateSubCategoriesUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockBean
    private DefaultGetCategoryByIdUseCase getCategoryByIdUseCase;

    @Test
    void givenAValidInput_whenCallCreateCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateCategoryRootOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameAlreadyExists_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = "Category already exists";

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthMoreThan255_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan1000_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(1001);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 1000);

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/categories")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCategoryRootCommand.class);

        Mockito.verify(createCategoryRootUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInput_whenCallUpdateSubCategories_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateSubCategoriesOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{categoryId}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameAlreadyExists_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidCategoryRootId_whenCallUpdateSubCategories_thenThrowNotFoundException() throws Exception {
        final var aCategoryId = "123";

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aCategoryId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{categoryId}/sub", aCategoryId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCategoryId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputBlankName_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthMoreThan255_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan1000_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(1001);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 1000);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{categoryId}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputSubCategoriesLevelIsMax_whenCallUpdateSubCategories_thenThrowDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("subCategories", 0, 5);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInputWithDescriptionAndSubCategoryId_whenCallUpdateSubCategories_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateSubCategoriesOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub/{subId}", aId, aSubId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertEquals(aSubId, actualCmd.subCategoryId().get());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInputAndInvalidSubCategoryId_whenCallUpdateSubCategories_thenThrowNotFoundException() throws Exception {
        final var aCategory = Fixture.Categories.home();

        final var aId = aCategory.getId().getValue();
        final var aSubId = "123";

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aSubId);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aSubId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub/{subId}", aId, aSubId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertEquals(aSubId, actualCmd.subCategoryId().get());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputAndValidSubCategoryId_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();

        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/sub/{subId}", aId, aSubId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateSubCategoriesCommand.class);

        Mockito.verify(updateSubCategoriesUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertEquals(aSubId, actualCmd.subCategoryId().get());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInput_whenCallUpdateRootCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCategoryOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInputWithSubCategoryId_whenCallUpdateSubCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCategoryOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{id}/update/{subId}", aId, aSubId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertEquals(aSubId, actualCmd.subCategoryId().get());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputAndValidSubCategoryId_whenCallUpdateSubCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();

        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/{categoryId}/update/{subId}", aId, aSubId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertEquals(aSubId, actualCmd.subCategoryId().get());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameAlreadyExists_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = "Category already exists";

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidCategoryId_whenCallUpdateCategory_thenThrowNotFoundException() throws Exception {
        final var aId = "123";

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Ca ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNameLengthMoreThan255_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan1000_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(1001);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 1000);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/categories/update/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCategoryCommand.class);

        Mockito.verify(updateCategoryUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.rootCategoryId());
        Assertions.assertTrue(actualCmd.subCategoryId().isEmpty());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aCommand = DeleteCategoryCommand.with(aId, null);

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aCommand);

        final var request = MockMvcRequestBuilders.delete("/v1/categories/delete/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aCommand);
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aId = "123";

        final var aCommand = DeleteCategoryCommand.with(aId, null);

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aCommand);

        final var request = MockMvcRequestBuilders.delete("/v1/categories/delete/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aCommand);
    }

    @Test
    void givenAValidSubCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        final var aId = aCategory.getId().getValue();
        final var aSubId = aSubCategory.getId().getValue();

        final var aCommand = DeleteCategoryCommand.with(aId, aSubId);

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aCommand);

        final var request = MockMvcRequestBuilders.delete("/v1/categories/{rootCategoryId}/delete/{subCategoryId}", aId, aSubId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aCommand);
    }

    @Test
    void givenAnInvalidSubCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aId = Fixture.Categories.home().getId().getValue();
        final var aSubId = "123";

        final var aCommand = DeleteCategoryCommand.with(aId, aSubId);

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aCommand);

        final var request = MockMvcRequestBuilders.delete("/v1/categories/{rootCategoryId}/delete/{subCategoryId}", aId, aSubId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aCommand);
    }

    @Test
    void givenValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        final var aCategory = Fixture.Categories.tech();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "tech";
        final var aSort = "description";
        final var aDirection = "desc";
        final var aItemsCount = 1;
        final var aTotalPages = 1;
        final var aTotal = 1;

        final var aItems = List.of(ListCategoriesOutput.from(aCategory));

        Mockito.when(listCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(new Pagination<>(aPage, aPerPage, aTotalPages, aTotal, aItems));

        final var request = MockMvcRequestBuilders.get("/v1/categories")
                .queryParam("page", String.valueOf(aPage))
                .queryParam("perPage", String.valueOf(aPerPage))
                .queryParam("sort", aSort)
                .queryParam("dir", aDirection)
                .queryParam("search", aTerms)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", equalTo(aPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", equalTo(aPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages", equalTo(aTotalPages)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_items", equalTo(aTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", hasSize(aItemsCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", equalTo(aCategory.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", equalTo(aCategory.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", equalTo(aCategory.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].slug", equalTo(aCategory.getSlug())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].parent_id", equalTo(null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].sub_categories", hasSize(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].created_at", equalTo(aCategory.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].updated_at", equalTo(aCategory.getUpdatedAt().toString())));

        Mockito.verify(listCategoriesUseCase, Mockito.times(1)).execute(argThat(query ->
                Objects.equals(aPage, query.page())
                        && Objects.equals(aPerPage, query.perPage())
                        && Objects.equals(aDirection, query.direction())
                        && Objects.equals(aSort, query.sort())
                        && Objects.equals(aTerms, query.terms())
        ));
    }

    @Test
    void givenAValidId_whenCallGetCategoryById_thenReturnCategory() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aSubCategory = Fixture.Categories.makeSubCategories(1, aCategory).stream()
                .findFirst().get();
        aCategory.addSubCategory(aSubCategory);
        aCategory.updateSubCategoriesLevel();

        final var aId = aCategory.getId().getValue();

        Mockito.when(getCategoryByIdUseCase.execute(Mockito.any()))
                .thenReturn(GetCategoryByIdOutput.from(aCategory));

        final var request = MockMvcRequestBuilders.get("/v1/categories/{id}", aId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(aCategory.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", equalTo(aCategory.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slug", equalTo(aCategory.getSlug())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parent_id", equalTo(null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.level", equalTo(aCategory.getLevel())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sub_categories[0].name", equalTo(aSubCategory.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", equalTo(aCategory.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", equalTo(aCategory.getUpdatedAt().toString())));

        Mockito.verify(getCategoryByIdUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAnInvalidId_whenCallGetCategoryById_thenThrowNotFoundException() throws Exception {
        final var aCategory = Fixture.Categories.home();

        final var aId = aCategory.getId().getValue();

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        Mockito.when(getCategoryByIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aId).get());

        final var request = MockMvcRequestBuilders.get("/v1/categories/{id}", aId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(getCategoryByIdUseCase, Mockito.times(1)).execute(aId);
    }
}
