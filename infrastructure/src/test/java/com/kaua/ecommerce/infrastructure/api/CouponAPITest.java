package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponOutput;
import com.kaua.ecommerce.application.usecases.coupon.deactivate.DeactivateCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.coupon.models.CreateCouponInput;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

    @Test
    void givenAValidInput_whenCallCreateCoupon_thenReturnStatusOkAndIdAndCode() throws Exception {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        final var aCode = aCoupon.getCode().getValue();
        final var aPercentage = 15.5f;
        final var aExpirationDate = InstantUtils.now().toString();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED.name();

        final var aInput = new CreateCouponInput(
                aCode,
                aPercentage,
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
        Assertions.assertEquals(aExpirationDate, actualCmd.expirationDate());
        Assertions.assertEquals(aIsActive, actualCmd.isActive());
        Assertions.assertEquals(aType, actualCmd.type());
        Assertions.assertEquals(0, actualCmd.maxUses());
    }

    @Test
    void givenAnInvalidAllInputParams_whenCallCreateCoupon_thenReturnDomainException() throws Exception {
        final var aCode = " ";
        final var aPercentage = -15.5f;
        final var aExpirationDate = InstantUtils.now().minus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED.name();

        final var aInput = new CreateCouponInput(
                aCode,
                aPercentage,
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
}
