package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.OnlyOneBannerImagePermittedException;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesOutput;
import com.kaua.ecommerce.application.usecases.product.attributes.add.AddProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.attributes.remove.RemoveProductAttributesUseCase;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductCommand;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductOutput;
import com.kaua.ecommerce.application.usecases.product.create.CreateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.delete.DeleteProductUseCase;
import com.kaua.ecommerce.application.usecases.product.media.remove.RemoveProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageCommand;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageOutput;
import com.kaua.ecommerce.application.usecases.product.media.upload.UploadProductImageUseCase;
import com.kaua.ecommerce.application.usecases.product.retrieve.details.GetProductDetailsBySkuUseCase;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdOutput;
import com.kaua.ecommerce.application.usecases.product.retrieve.get.GetProductByIdUseCase;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsOutput;
import com.kaua.ecommerce.application.usecases.product.search.retrieve.list.ListProductsUseCase;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductCommand;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductOutput;
import com.kaua.ecommerce.application.usecases.product.update.UpdateProductUseCase;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusCommand;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusOutput;
import com.kaua.ecommerce.application.usecases.product.update.status.UpdateProductStatusUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductImageType;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.exceptions.ImageSizeNotValidException;
import com.kaua.ecommerce.infrastructure.exceptions.ImageTypeNotValidException;
import com.kaua.ecommerce.infrastructure.product.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;

@ControllerTest(controllers = ProductAPI.class)
public class ProductAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private UploadProductImageUseCase uploadProductImageUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @MockBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockBean
    private UpdateProductStatusUseCase updateProductStatusUseCase;

    @MockBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockBean
    private ListProductsUseCase listProductsUseCase;

    @MockBean
    private RemoveProductImageUseCase removeProductImageUseCase;

    @MockBean
    private AddProductAttributesUseCase addProductAttributesUseCase;

    @MockBean
    private RemoveProductAttributesUseCase removeProductAttributesUseCase;

    @MockBean
    private GetProductDetailsBySkuUseCase getProductDetailsBySkuUseCase;

    @Test
    void givenAValidInput_whenCallCreateProduct_thenReturnStatusOkAndProductId() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Tshirt Test";
        final var aDescription = "Tshirt Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final String aName = null;
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("name");

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Pr ";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputNameLengthMoreThan255_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = RandomStringUtils.generateValue(256);
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan3000_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputPriceSmallerOrEqualZero_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(0.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputQuantityLessThanZero_whenCallCreateProduct_thenReturnDomainException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = -1;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAnInvalidInputCategoryId_whenCallCreateProduct_thenThrowNotFoundException() throws Exception {
        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aQuantity = 10;
        final var aCategoryId = "123";
        final var aColorName = "Red";
        final var aSizeName = "M";
        final var aWeight = 0.5;
        final var aHeight = 0.5;
        final var aWidth = 0.5;
        final var aLength = 0.5;

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aAttributesInput = new CreateProductInputAttributes(
                aColorName,
                aSizeName,
                aWeight,
                aHeight,
                aWidth,
                aLength,
                aQuantity
        );

        final var aInput = new CreateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId,
                List.of(aAttributesInput)
        );

        Mockito.when(createProductUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aCategoryId).get());

        final var request = MockMvcRequestBuilders.post("/v1/products")
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
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
        Assertions.assertEquals(1, actualCmd.attributes().size());
    }

    @Test
    void givenAValidProductIdAndBannerImage_whenCallUploadProductImage_thenShouldReturnOkAndProductIdAndType() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aType = ProductImageType.BANNER;

        final var aImage = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                "image".getBytes()
        );

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenReturn(UploadProductImageOutput.from(aProduct));

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImage)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UploadProductImageCommand.class);

        Mockito.verify(uploadProductImageUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.productId());
        Assertions.assertEquals(aType, actualCmd.productImagesResources().stream().findFirst().get().type());
        Assertions.assertEquals(aImage.getOriginalFilename(), actualCmd.productImagesResources()
                .stream().findFirst().get().resource().fileName());
        Assertions.assertEquals(aImage.getContentType(), actualCmd.productImagesResources()
                .stream().findFirst().get().resource().contentType());
    }

    @Test
    void givenAValidProductIdAndGalleryImages_whenCallUploadProductImage_thenShouldReturnOkAndProductIdAndType() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aType = ProductImageType.GALLERY;

        final var aImageOne = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                "image".getBytes()
        );
        final var aImageTwo = new MockMultipartFile(
                "media_file",
                "product-two.png",
                "image/png",
                "image-two".getBytes()
        );

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenReturn(UploadProductImageOutput.from(aProduct));

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImageOne)
                .file(aImageTwo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UploadProductImageCommand.class);

        Mockito.verify(uploadProductImageUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.productId());
        Assertions.assertEquals(aType, actualCmd.productImagesResources().stream().findFirst().get().type());
        Assertions.assertEquals(2, actualCmd.productImagesResources().size());
    }

    @Test
    void givenAValidProductIdAndInvalidTwoBannerImage_whenCallUploadProductImage_thenShouldThrowDomainException() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aType = ProductImageType.BANNER;

        final var aImageOne = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                "image".getBytes()
        );
        final var aImageTwo = new MockMultipartFile(
                "media_file",
                "product-two.png",
                "image/png",
                "image-two".getBytes()
        );

        final var expectedErrorMessage = "Only one banner image is allowed";

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenThrow(new OnlyOneBannerImagePermittedException());

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImageOne)
                .file(aImageTwo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(uploadProductImageUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidImageSize_whenCallUploadProductImage_thenShouldThrowDomainException() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aType = ProductImageType.BANNER;

        final var aImage = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                new byte[600 * 1024 + 2]
        );

        final var expectedErrorMessage = "Maximum image size is 600kb";

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenThrow(new ImageSizeNotValidException());

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImage)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(uploadProductImageUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidImageType_whenCallUploadProductImage_thenShouldThrowDomainException() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aType = ProductImageType.BANNER;

        final var aImage = new MockMultipartFile(
                "media_file",
                "product.mp4",
                "video/mp4",
                "product".getBytes()
        );

        final var expectedErrorMessage = "Image type is not valid, types accept: jpg, jpeg and png";

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenThrow(new ImageTypeNotValidException());

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImage)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(uploadProductImageUseCase, Mockito.times(0)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallUploadProductImage_thenShouldThrowNotFoundException() throws Exception {
        // given
        final var aId = "123";
        final var aType = ProductImageType.BANNER;

        final var aImage = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                "product".getBytes()
        );
        final var expectedErrorMessage = "Product with id 123 was not found";

        Mockito.when(uploadProductImageUseCase.execute(Mockito.any(UploadProductImageCommand.class)))
                .thenThrow(NotFoundException.with(Product.class, aId).get());

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/{type}", aId, aType.name())
                .file(aImage)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UploadProductImageCommand.class);

        Mockito.verify(uploadProductImageUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.productId());
        Assertions.assertEquals(aType, actualCmd.productImagesResources().stream().findFirst().get().type());
        Assertions.assertEquals(aImage.getOriginalFilename(), actualCmd.productImagesResources()
                .stream().findFirst().get().resource().fileName());
        Assertions.assertEquals(aImage.getContentType(), actualCmd.productImagesResources()
                .stream().findFirst().get().resource().contentType());
    }

    @Test
    void givenAnInvalidType_whenCallUploadProductImage_thenShouldReturnDomainException() throws Exception {
        // given
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aImage = new MockMultipartFile(
                "media_file",
                "product.png",
                "image/png",
                "product".getBytes()
        );
        final var expectedErrorMessage = "type INVALID was not found";

        final var request = MockMvcRequestBuilders.multipart(HttpMethod.POST, "/v1/products/{id}/medias/INVALID", aId)
                .file(aImage)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidInput_whenCallUpdateProduct_thenReturnStatusOkAndProductId() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Product Test";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(59.98);
        final var aCategoryId = "123";

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputNullName_whenCallUpdateProduct_thenReturnStatusOkAndProductIdAndNotUpdateName() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final String aName = null;
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = "123";

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertNull(actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputNameLengthLessThan3_whenCallUpdateProduct_thenReturnDomainException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Pr ";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = "123";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("name", 3, 255);

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputDescriptionLengthMoreThan3000_whenCallUpdateProduct_thenReturnDomainException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Product Name";
        final var aDescription = RandomStringUtils.generateValue(3001);
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = "123";

        final var expectedErrorMessage = CommonErrorMessage.lengthBetween("description", 0, 3000);

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputPriceSmallerOrEqualZero_whenCallUpdateProduct_thenReturnDomainException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(0.0);
        final var aCategoryId = "123";

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputNullCategoryId_whenCallUpdateProduct_thenReturnStatusOkAndProductIdAndNotUpdateCateogry() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final String aCategoryId = null;

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateProductOutput.from(aProduct)));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertNull(actualCmd.categoryId());
    }

    @Test
    void givenAnInvalidInputNotExistsCategoryId_whenCallUpdateProduct_thenThrowNotFoundException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        final var aName = "Product Name";
        final var aDescription = "Product Test Description";
        final var aPrice = BigDecimal.valueOf(10.0);
        final var aCategoryId = "123";

        final var expectedErrorMessage = Fixture.notFoundMessage(Category.class, aCategoryId);

        final var aInput = new UpdateProductInput(
                aName,
                aDescription,
                aPrice,
                aCategoryId
        );

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Category.class, aCategoryId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductCommand.class);

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aName, actualCmd.name());
        Assertions.assertEquals(aDescription, actualCmd.description());
        Assertions.assertEquals(aPrice, actualCmd.price());
        Assertions.assertEquals(aCategoryId, actualCmd.categoryId());
    }

    @Test
    void givenAValidProductId_whenCallDeleteProduct_thenShouldBeOk() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();

        Mockito.doNothing().when(deleteProductUseCase).execute(Mockito.anyString());

        final var request = MockMvcRequestBuilders.delete("/v1/products/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteProductUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallDeleteProduct_thenShouldBeOk() throws Exception {
        final var aId = "INVALID";

        Mockito.doNothing().when(deleteProductUseCase).execute(Mockito.anyString());

        final var request = MockMvcRequestBuilders.delete("/v1/products/{id}", aId);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteProductUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallUpdateProductStatus_thenReturnStatusOkAndProductId() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aId = aProduct.getId().getValue();
        final var aStatus = "inactive";

        Mockito.when(updateProductStatusUseCase.execute(Mockito.any()))
                .thenReturn(UpdateProductStatusOutput.from(aProduct));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}/{status}", aId, aStatus)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(aId)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductStatusCommand.class);

        Mockito.verify(updateProductStatusUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aStatus, actualCmd.status());
    }

    @Test
    void givenAnInvalidProductId_whenCallUpdateProductStatus_thenThrowNotFoundException() throws Exception {
        final var aId = "1";
        final var aStatus = "inactive";

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aId);

        Mockito.when(updateProductStatusUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Product.class, aId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}/{status}", aId, aStatus)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductStatusCommand.class);

        Mockito.verify(updateProductStatusUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aStatus, actualCmd.status());
    }

    @Test
    void givenAnInvalidStatus_whenCallUpdateProductStatus_thenThrowDomainException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aId = aProduct.getId().getValue();
        final var aStatus = "INVALID";

        final var expectedErrorMessage = "status INVALID was not found";

        Mockito.when(updateProductStatusUseCase.execute(Mockito.any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}/{status}", aId, aStatus)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductStatusCommand.class);

        Mockito.verify(updateProductStatusUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aStatus, actualCmd.status());
    }

    @Test
    void givenAnInvalidStatusIsDeleted_whenCallUpdateProductStatus_thenThrowDomainException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aId = aProduct.getId().getValue();
        final var aStatus = "deleted";

        final var expectedErrorMessage = "status DELETED is not allowed to be set";

        Mockito.when(updateProductStatusUseCase.execute(Mockito.any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}/{status}", aId, aStatus)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateProductStatusCommand.class);

        Mockito.verify(updateProductStatusUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aId, actualCmd.id());
        Assertions.assertEquals(aStatus, actualCmd.status());
    }

    @Test
    void givenAValidProduct_whenCallUpdateProductUseCase_butThrowsTransactionFailureExceptionBecauseOptimisticLock() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aInput = new UpdateProductInput(
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getCategoryId().getValue()
        );

        final var expectedErrorMessage = "An optimistic lock exception occurred while processing the transaction";

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenThrow(TransactionFailureException.with(new Error("Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)")));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aProduct.getId().getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidProduct_whenCallUpdateProductUseCase_butThrowsTransactionFailureExceptionBecauseUnexpectedException() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aInput = new UpdateProductInput(
                aProduct.getName(),
                aProduct.getDescription(),
                aProduct.getPrice(),
                aProduct.getCategoryId().getValue()
        );

        final var expectedErrorMessage = "An error occurred while processing the transaction";

        Mockito.when(updateProductUseCase.execute(Mockito.any()))
                .thenThrow(TransactionFailureException.with(new Error("Transaction failed")));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}", aProduct.getId().getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(updateProductUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductId_whenCallGetProductByIdUseCase_shouldReturnProduct() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        aProduct.changeBannerImage(Fixture.Products.productImage(ProductImageType.BANNER));
        aProduct.addImage(Fixture.Products.productImage(ProductImageType.GALLERY));

        final var aId = aProduct.getId().getValue();

        Mockito.when(getProductByIdUseCase.execute(Mockito.any()))
                .thenReturn(GetProductByIdOutput.from(aProduct));

        final var request = MockMvcRequestBuilders.get("/v1/products/{id}", aId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(aProduct.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", equalTo(aProduct.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", equalTo(aProduct.getPrice().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category_id", equalTo(aProduct.getCategoryId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banner_image.location", equalTo(aProduct.getBannerImage().get().getLocation())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gallery_images", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", equalTo(aProduct.getStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", equalTo(aProduct.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", equalTo(aProduct.getUpdatedAt().toString())));

        Mockito.verify(getProductByIdUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallGetProductByIdUseCase_shouldThrowNotFoundException() throws Exception {
        final var aId = "123";

        final var expectedErrorMessage = Fixture.notFoundMessage(Product.class, aId);

        Mockito.when(getProductByIdUseCase.execute(Mockito.any()))
                .thenThrow(NotFoundException.with(Product.class, aId).get());

        final var request = MockMvcRequestBuilders.get("/v1/products/{id}", aId)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(getProductByIdUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenValidParams_whenCallsListProducts_shouldReturnProducts() throws Exception {
        final var aProduct = Fixture.Products.book();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "livro";
        final var aSort = "description";
        final var aDirection = "desc";
        final var aItemsCount = 1;
        final var aTotalPages = 1;
        final var aTotal = 1;

        final var aItems = List.of(ListProductsOutput.from(aProduct));

        Mockito.when(listProductsUseCase.execute(Mockito.any()))
                .thenReturn(new Pagination<>(aPage, aPerPage, aTotalPages, aTotal, aItems));

        final var request = MockMvcRequestBuilders.get("/v1/products")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].product_id", equalTo(aProduct.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", equalTo(aProduct.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", equalTo(aProduct.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].price", equalTo(aProduct.getPrice().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].category_id", equalTo(aProduct.getCategoryId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].banner_image", equalTo(null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].status", equalTo(aProduct.getStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].created_at", equalTo(aProduct.getUpdatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].updated_at", equalTo(aProduct.getUpdatedAt().toString())));

        Mockito.verify(listProductsUseCase, Mockito.times(1)).execute(argThat(query ->
                Objects.equals(aPage, query.page())
                        && Objects.equals(aPerPage, query.perPage())
                        && Objects.equals(aDirection, query.direction())
                        && Objects.equals(aSort, query.sort())
                        && Objects.equals(aTerms, query.terms())
        ));
    }

    @Test
    void givenAValidProductIdAndLocation_whenCallDeleteProductImage_thenShouldBeOk() throws Exception {
        final var aProductImage = Fixture.Products.productImage(ProductImageType.BANNER);
        final var aProduct = Fixture.Products.tshirt();
        final var aId = aProduct.getId().getValue();
        final var aLocation = aProductImage.getLocation();

        aProduct.changeBannerImage(aProductImage);

        Mockito.doNothing().when(removeProductImageUseCase).execute(Mockito.any());

        final var request = MockMvcRequestBuilders.delete("/v1/products/{id}/medias/{location}", aId, aLocation);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(removeProductImageUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenValidParams_whenCallsAddProductAttributes_shouldReturnProductId() throws Exception {
        final var aProduct = Fixture.Products.book();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());

        final var aId = aProduct.getId().getValue();
        final var aQuantity = 10;

        final var aInput = new AddProductAttributesInput(
                List.of(new AddProductAttributesParamInput(
                        aProductAttribute.getColor().getColor(),
                        aProductAttribute.getSize().getSize(),
                        aProductAttribute.getSize().getWeight(),
                        aProductAttribute.getSize().getHeight(),
                        aProductAttribute.getSize().getWidth(),
                        aProductAttribute.getSize().getLength(),
                        aQuantity
                ))
        );

        Mockito.when(addProductAttributesUseCase.execute(Mockito.any()))
                .thenReturn(AddProductAttributesOutput.from(aProduct));

        final var request = MockMvcRequestBuilders.patch("/v1/products/{id}/attributes", aId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.product_id", equalTo(aId)));

        Mockito.verify(addProductAttributesUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidProductIdAndSku_whenCallDeleteProductSku_thenShouldBeOk() throws Exception {
        final var aProduct = Fixture.Products.tshirt();
        final var aProductAttribute = Fixture.Products.productAttributes(aProduct.getName());
        aProduct.addAttribute(aProductAttribute);

        final var aId = aProduct.getId().getValue();
        final var aSku = aProductAttribute.getSku();

        Mockito.doNothing().when(removeProductAttributesUseCase).execute(Mockito.any());

        final var request = MockMvcRequestBuilders.delete("/v1/products/{id}/attributes/{sku}", aId, aSku);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(removeProductAttributesUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidSku_whenCallGetProductDetailsBySku_shouldReturnProductDetails() throws Exception {
        final var aProduct = Fixture.Products.tshirt();

        final var aAttribute = aProduct.getAttributes().stream().findFirst().get();
        final var aSku = aAttribute.getSku();

        final var aProductDetails = new ProductDetails(
                aSku,
                aProduct.getPrice(),
                aAttribute.getSize().getWeight(),
                aAttribute.getSize().getWidth(),
                aAttribute.getSize().getHeight(),
                aAttribute.getSize().getLength()
        );

        Mockito.when(getProductDetailsBySkuUseCase.execute(Mockito.any()))
                .thenReturn(aProductDetails);

        final var request = MockMvcRequestBuilders.get("/v1/products/details/{sku}", aSku)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku", equalTo(aSku)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", equalTo(aProduct.getPrice().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.weight").value(is(aAttribute.getSize().getWeight()), Double.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.width").value(is(aAttribute.getSize().getWidth()), Double.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.height").value(is(aAttribute.getSize().getHeight()), Double.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length").value(is(aAttribute.getSize().getLength()), Double.class));

        Mockito.verify(getProductDetailsBySkuUseCase, Mockito.times(1)).execute(Mockito.any());
    }
}
