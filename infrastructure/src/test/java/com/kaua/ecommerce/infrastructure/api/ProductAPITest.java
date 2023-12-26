package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductOutput;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.product.models.CreateProductInput;
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

@ControllerTest(controllers = ProductAPI.class)
public class ProductAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @Test
    void givenAValidInputWithDescription_whenCallCreateProduct_thenReturnStatusOkAndProductId() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Tshirt Test";
        final var aDescription = "Tshirt Test Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAValidInputWithoutDescription_whenCallCreateProduct_thenReturnStatusOkAndProductId() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Tshirt Test";
        final String aDescription = null;
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final String aName = null;
        final var aDescription = "Product Test Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputBlankName_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = " ";
        final var aDescription = "Product Test Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Pr ";
        final var aDescription = "Product Test Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputNameLengthMoreThan255_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Product Test Description";
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan255_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(256);
        final var aPrice = 10.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 255);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputPriceSmallerOrEqualZero_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = 0.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputQuantityLessThanZero_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = 10.0;
        final var aQuantity = -1;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }

    @Test
    void givenAnInvalidInputCategoryId_whenCallCreateProduct_thenThrowNotFoundException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = 0.0;
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aDepth = 0.5;

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aQuantity,
                aCategoryId,
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aDepth
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aCategoryId).get());

        final var request = MockMvcRequestBuilders.post("/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateProductCommand.class);

        Mockito.verify(createProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aQuantity, actualCmd.quantity());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(aColorName, actualCmd.colorName());
        Assertions.assertEquals(aSizeName, actualCmd.sizeName());
        Assertions.assertEquals(aWeight, actualCmd.weight());
        Assertions.assertEquals(aHeight, actualCmd.height());
        Assertions.assertEquals(aWidth, actualCmd.width());
        Assertions.assertEquals(aDepth, actualCmd.depth());
    }
}
