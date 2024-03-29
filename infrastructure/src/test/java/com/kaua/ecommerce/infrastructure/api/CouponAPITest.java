package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.delete.DeleteCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.ListCouponsOutput;
import com.kaua.ecommerce.application.usecases.coupon.retrieve.list.ListCouponsUseCase;
import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.update.UpdateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.validate.ValidateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.validate.ValidateCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.coupon.models.CreateCouponInput;
import com.kaua.ecommerce.infrastructure.coupon.models.UpdateCouponInput;
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

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;

@ControllerTest(controllers = CouponAPI.class)
public class CouponAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCouponUseCase createCouponUseCase;

    @MockBean
    private ActivateCouponUseCase activateCouponUseCase;

    @MockBean
    private DeactivateCouponUseCase deactivateCouponUseCase;

    @MockBean
    private DeleteCouponUseCase deleteCouponUseCase;

    @MockBean
    private ValidateCouponUseCase validateCouponUseCase;

    @MockBean
    private ApplyCouponUseCase applyCouponUseCase;

    @MockBean
    private ListCouponsUseCase listCouponsUseCase;

    @MockBean
    private UpdateCouponUseCase updateCouponUseCase;

    @Test
    void givenAValidInput_whenCallCreateCoupon_thenReturnStatusOkAndIdAndCode() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aCode = aCoupon.getCode().getValue();
        final var aPercentage = 15.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().toString();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED.name();

        final var aInput = new CreateCouponInput(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(createCouponUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateCouponOutput.from(aCoupon)));

        final var request = MockMvcRequestBuilders.post("/v1/coupons")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", equalTo(aCode)));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCouponCommand.class);

        Mockito.verify(createCouponUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCode, actualCmd.code());
        Assertions.assertEquals(aPercentage, actualCmd.percentage());
        Assertions.assertEquals(aMinimumPurchase, actualCmd.minimumPurchase());
        Assertions.assertEquals(aExpirationDate, actualCmd.expirationDate());
        Assertions.assertEquals(aIsActive, actualCmd.isActive());
        Assertions.assertEquals(aType, actualCmd.type());
        Assertions.assertEquals(0, actualCmd.maxUses());
    }

    @Test
    void givenAnInvalidAllInputParams_whenCallCreateCoupon_thenReturnDomainException() throws Exception {
        final var aCode = " ";
        final var aPercentage = -15.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().minus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED.name();

        final var aInput = new CreateCouponInput(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        final var expectedErrors = List.of(
                CommonErrorMessage.nullOrBlank("code"),
                CommonErrorMessage.greaterThan("percentage", 0),
                CommonErrorMessage.dateMustBeFuture("expirationDate")
        );

        final var aNotification = NotificationHandler.create();
        expectedErrors.forEach(it -> aNotification.append(new Error(it)));

        Mockito.when(createCouponUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(aNotification));

        final var request = MockMvcRequestBuilders.post("/v1/coupons")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(expectedErrors.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrors.get(0))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[1].message", equalTo(expectedErrors.get(1))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[2].message", equalTo(expectedErrors.get(2))));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateCouponCommand.class);

        Mockito.verify(createCouponUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCode, actualCmd.code());
        Assertions.assertEquals(aPercentage, actualCmd.percentage());
        Assertions.assertEquals(aExpirationDate, actualCmd.expirationDate());
        Assertions.assertEquals(aIsActive, actualCmd.isActive());
        Assertions.assertEquals(aType, actualCmd.type());
        Assertions.assertEquals(0, actualCmd.maxUses());
    }

    @Test
    void givenAValidId_whenCallActivateCoupon_thenReturnStatusOkAndCouponActivated() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aId = aCoupon.getId().getValue();

        Mockito.when(activateCouponUseCase.execute(aId))
                .thenReturn(ActivateCouponOutput.from(aCoupon));

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/activate/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", equalTo(aCoupon.getCode().getValue())));

        Mockito.verify(activateCouponUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAnInvalidId_whenCallActivateCoupon_thenReturnDomainException() throws Exception {
        final var aId = "invalid-id";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aId);

        Mockito.when(activateCouponUseCase.execute(aId))
                .thenThrow(NotFoundException.with(Coupon.class, aId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/activate/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(activateCouponUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAValidId_whenCallDeactivateCoupon_thenReturnStatusOkAndCouponDeactivated() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aId = aCoupon.getId().getValue();

        Mockito.when(deactivateCouponUseCase.execute(aId))
                .thenReturn(DeactivateCouponOutput.from(aCoupon));

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/deactivate/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", equalTo(aCoupon.getCode().getValue())));

        Mockito.verify(deactivateCouponUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAnInvalidId_whenCallDeactivateCoupon_thenReturnDomainException() throws Exception {
        final var aId = "invalid-id";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aId);

        Mockito.when(deactivateCouponUseCase.execute(aId))
                .thenThrow(NotFoundException.with(Coupon.class, aId).get());

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/deactivate/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo(expectedErrorMessage)));

        Mockito.verify(deactivateCouponUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAValidId_whenCallDeleteCoupon_thenReturnStatusOk() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aId = aCoupon.getId().getValue();

        Mockito.doNothing().when(deleteCouponUseCase).execute(aId);

        final var request = MockMvcRequestBuilders.delete("/v1/coupons/{id}", aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(deleteCouponUseCase, Mockito.times(1)).execute(aId);
    }

    @Test
    void givenAValidCouponCode_whenCallValidateCouponByCode_thenReturnStatusOkAndCouponValid() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aCode = aCoupon.getCode().getValue();

        Mockito.when(validateCouponUseCase.execute(aCode))
                .thenReturn(ValidateCouponOutput.from(aCoupon, true));

        final var request = MockMvcRequestBuilders.get("/v1/coupons/validate/{code}", aCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_code", equalTo(aCoupon.getCode().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_percentage", is((double) aCoupon.getPercentage())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_valid", equalTo(true)));

        Mockito.verify(validateCouponUseCase, Mockito.times(1)).execute(aCode);
    }

    @Test
    void givenAValidCode_whenCallApplyCoupon_thenReturnStatusOkAndCouponSlotRemoved() throws Exception {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCouponCode = aCoupon.getCode().getValue();

        final var aCommand = ApplyCouponCommand.with(aCouponCode, 100f);

        Mockito.when(applyCouponUseCase.execute(aCommand))
                .thenReturn(ApplyCouponOutput.from(aCoupon));

        final var request = MockMvcRequestBuilders.delete("/v1/coupons/slots/{code}", aCouponCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_code", equalTo(aCoupon.getCode().getValue())));

        Mockito.verify(applyCouponUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenValidParams_whenCallsListCoupons_shouldReturnCoupons() throws Exception {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "LIMITED";
        final var aSort = "code";
        final var aDirection = "desc";
        final var aItemsCount = 1;
        final var aTotalPages = 1;
        final var aTotal = 1;

        final var aItems = List.of(ListCouponsOutput.from(aCoupon));

        Mockito.when(listCouponsUseCase.execute(Mockito.any()))
                .thenReturn(new Pagination<>(aPage, aPerPage, aTotalPages, aTotal, aItems));

        final var request = MockMvcRequestBuilders.get("/v1/coupons")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].code", equalTo(aCoupon.getCode().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].percentage", equalTo((double) aCoupon.getPercentage())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].expiration_date", equalTo(aCoupon.getExpirationDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].is_active", equalTo(aCoupon.isActive())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].type", equalTo(aCoupon.getType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].created_at", equalTo(aCoupon.getCreatedAt().toString())));

        Mockito.verify(listCouponsUseCase, Mockito.times(1)).execute(argThat(query ->
                Objects.equals(aPage, query.page())
                        && Objects.equals(aPerPage, query.perPage())
                        && Objects.equals(aDirection, query.direction())
                        && Objects.equals(aSort, query.sort())
                        && Objects.equals(aTerms, query.terms())
        ));
    }

    @Test
    void givenAValidValues_whenCallUpdateCoupon_thenReturnStatusOkAndCouponUpdated() throws Exception {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().plus(5, ChronoUnit.DAYS).toString();

        final var aInput = new UpdateCouponInput(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate
        );

        Mockito.when(updateCouponUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(UpdateCouponOutput.from(Coupon.with(
                        aCoupon.getId().getValue(),
                        aCode,
                        aPercentage,
                        aMinimumPurchase,
                        aCoupon.getExpirationDate(),
                        aCoupon.isActive(),
                        aCoupon.getType(),
                        aCoupon.getCreatedAt(),
                        aCoupon.getUpdatedAt(),
                        aCoupon.getVersion()))));

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/{id}", aCouponId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_id", equalTo(aCoupon.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", equalTo(aCode)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCouponCommand.class);

        Mockito.verify(updateCouponUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCode, actualCmd.code());
        Assertions.assertEquals(aPercentage, actualCmd.percentage());
        Assertions.assertEquals(aExpirationDate, actualCmd.expirationDate());
    }

    @Test
    void givenAnInvalidNullCode_whenCallUpdateCouponById_thenReturnDomainException() throws Exception {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCouponId = aCoupon.getId().getValue();

        final String aCode = null;
        final var aPercentage = 200.0f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().plus(5, ChronoUnit.DAYS).toString();

        final var aInput = new UpdateCouponInput(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");

        Mockito.when(updateCouponUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.patch("/v1/coupons/{id}", aCouponId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateCouponCommand.class);

        Mockito.verify(updateCouponUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCode, actualCmd.code());
        Assertions.assertEquals(aPercentage, actualCmd.percentage());
        Assertions.assertEquals(aExpirationDate, actualCmd.expirationDate());
    }
}
