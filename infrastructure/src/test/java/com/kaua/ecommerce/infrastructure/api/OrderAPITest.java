package com.kaua.ecommerce.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderCommand;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderOutput;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderUseCase;
import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeOutput;
import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;
import com.kaua.ecommerce.infrastructure.ControllerTest;
import com.kaua.ecommerce.infrastructure.idempotency.IdempotencyKey;
import com.kaua.ecommerce.infrastructure.order.models.CreateOrderInput;
import com.kaua.ecommerce.infrastructure.order.models.CreateOrderItemInput;
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

import static org.hamcrest.Matchers.*;

@ControllerTest(controllers = OrderAPI.class)
public class OrderAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderByCodeUseCase getOrderByCodeUseCase;

    @Test
    void givenAValidInput_whenCallCreateOrder_thenReturnStatusOkAndOrderId() throws Exception {
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aCustomerId = "123";
        final var aCouponCode = "COUPON";
        final var aFreightType = "PAC";
        final var aPaymentMethodId = "456";
        final var aInstallments = 0;
        final var aItems = List.of(
                new CreateOrderItemInput("123", "sku-123", 1),
                new CreateOrderItemInput("456", "sku-456", 2)
        );

        final var aInput = new CreateOrderInput(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(createOrderUseCase.execute(Mockito.any()))
                .thenReturn(Either.right(CreateOrderOutput.from(aOrder)));

        final var request = MockMvcRequestBuilders.post("/v1/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IdempotencyKey.IDEMPOTENCY_KEY_HEADER, "123456")
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_id", equalTo(aOrder.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_code", equalTo(aOrder.getOrderCode().getValue())));

        final var cmdCaptor = ArgumentCaptor.forClass(CreateOrderCommand.class);

        Mockito.verify(createOrderUseCase, Mockito.times(1)).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(aCustomerId, actualCmd.customerId());
        Assertions.assertEquals(aCouponCode, actualCmd.couponCode());
        Assertions.assertEquals(aFreightType, actualCmd.freightType());
        Assertions.assertEquals(aPaymentMethodId, actualCmd.paymentMethodId());
        Assertions.assertEquals(aInstallments, actualCmd.installments());
        Assertions.assertEquals(aItems.size(), actualCmd.items().size());
    }

    @Test
    void givenAnInvalidInputWithNullPaymentMethodId_whenCallCreateOrder_thenReturnStatusProcessableEntity() throws Exception {
        final var aCustomerId = "123";
        final var aCouponCode = "COUPON";
        final var aFreightType = "PAC";
        final String aPaymentMethodId = null;
        final var aInstallments = 0;
        final var aItems = List.of(
                new CreateOrderItemInput("123", "sku-123", 1),
                new CreateOrderItemInput("456", "sku-456", 2)
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("paymentMethodId");
        final var expectedErrorCount = 1;

        final var aInput = new CreateOrderInput(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(createOrderUseCase.execute(Mockito.any()))
                .thenReturn(Either.left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = MockMvcRequestBuilders.post("/v1/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IdempotencyKey.IDEMPOTENCY_KEY_HEADER, "123456")
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidOrderCode_whenCallGetOrderByCode_thenReturnStatusOkAndOrder() throws Exception {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aOrderCode = aOrder.getOrderCode().getValue();

        Mockito.when(getOrderByCodeUseCase.execute(Mockito.any()))
                .thenReturn(GetOrderByCodeOutput.from(aOrder, aOrderDelivery, aOrderPayment));

        final var request = MockMvcRequestBuilders.get("/v1/orders/{code}", aOrderCode)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_id", equalTo(aOrder.getId().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_code", equalTo(aOrder.getOrderCode().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_status", equalTo(aOrder.getOrderStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer_id", equalTo(aOrder.getCustomerId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_code", equalTo(aOrder.getCouponCode().orElse(null))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coupon_percentage").value(is(aOrder.getCouponPercentage()), Float.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_amount", equalTo(aOrder.getTotalAmount().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at", equalTo(aOrder.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at", equalTo(aOrder.getUpdatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(is(aOrder.getVersion()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_items", hasSize(aOrder.getOrderItems().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_delivery").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.order_payment").exists());
    }
}
