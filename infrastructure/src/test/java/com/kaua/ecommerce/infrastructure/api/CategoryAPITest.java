package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootCommand;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootOutput;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

    @Test
    void givenAValidInputWithDescription_whenCallCreateCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateCategoryRootOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.post("/categories")
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
    void givenAValidInputWithoutDescription_whenCallCreateCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final String aDescription = null;

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateCategoryRootOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.post("/categories")
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

        final var request = MockMvcRequestBuilders.post("/categories")
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

        final var request = MockMvcRequestBuilders.post("/categories")
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
    void givenAnInvalidInputBlankName_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = " ";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/categories")
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

        final var request = MockMvcRequestBuilders.post("/categories")
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

        final var request = MockMvcRequestBuilders.post("/categories")
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
    void givenAnInvalidInputDescriptionLengthMoreThan255_whenCallCreateCategory_thenReturnDomainException() throws Exception {
        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aInput = new CreateCategoryInput(aName, aDescription);

        Mockito.when(createCategoryRootUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/categories")
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
    void givenAValidInputWithDescription_whenCallUpdateSubCategories_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateSubCategoriesOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInputWithoutDescription_whenCallUpdateSubCategories_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final String aDescription = null;

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateSubCategoriesOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidCategoryRootId_whenCallUpdateSubCategories_thenThrowNotFoundException() throws Exception {
        final var aId = "123";

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aId);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aId).get());

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final String aName = null;
        final var aDescription = "Category Test Description";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan255_whenCallUpdateSubCategories_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateSubCategoriesUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}/sub", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidInputWithDescription_whenCallUpdateCategory_thenReturnStatusOkAndCategoryId() throws Exception {
        final var aCategory = Fixture.Categories.home();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = "Category Test Description";

        final var aInput = new UpdateSubCategoriesInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCategoryOutput.from(aCategory)));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
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

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan255_whenCallUpdateCategory_thenReturnDomainException() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        final var aName = "Category Test";
        final var aDescription = RandomStringUtils.generateValue(256);

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aInput = new UpdateCategoryInput(aName, aDescription);

        Mockito.when(updateCategoryUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/categories/{id}", aId)
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

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
    }

    @Test
    void givenAValidCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aCategory = Fixture.Categories.tech();
        final var aId = aCategory.getId().getValue();

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aId);

        final var request = MockMvcRequestBuilders.delete("/categories/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAnInvalidCategoryId_whenCallDeleteCategory_shouldBeOk() throws Exception {
        final var aId = "123";

        Mockito.doNothing().when(deleteCategoryUseCase).execute(aId);

        final var request = MockMvcRequestBuilders.delete("/categories/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCategoryUseCase, Mockito.times(1)).execute(aId);
    }
}
