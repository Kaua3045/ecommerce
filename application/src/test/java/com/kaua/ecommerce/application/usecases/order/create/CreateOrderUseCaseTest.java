package com.kaua.ecommerce.application.usecases.order.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.order.*;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.order.OrderStatus;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateOrderUseCaseTest extends UseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderCustomerGateway orderCustomerGateway;

    @Mock
    private OrderCouponGateway orderCouponGateway;

    @Mock
    private OrderDeliveryGateway orderDeliveryGateway;

    @Mock
    private OrderPaymentGateway orderPaymentGateway;

    @Mock
    private OrderProductGateway orderProductGateway;

    @Mock
    private OrderFreightGateway orderFreightGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultCreateOrderUseCase createOrderUseCase;

    @Test
    void givenAValidCommand_whenCallCreateOrderUseCase_thenShouldCreateOrder() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = aCustomer.getAccountId();
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("bola", BigDecimal.valueOf(200.0))));
        Mockito.when(orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(createOrderFreightDetails());
        Mockito.when(orderCouponGateway.applyCoupon(aCouponCode))
                .thenReturn(createOrderCouponOutput(aCoupon));
        Mockito.when(orderGateway.count())
                .thenReturn(1L);
        Mockito.when(orderGateway.createInBatch(Mockito.anySet()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderDeliveryGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderPaymentGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any()))
                .thenAnswer(it -> TransactionResult.success(it.getArgument(0, Supplier.class).get()));

        final var aOutput = this.createOrderUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.orderId());
        Assertions.assertNotNull(aOutput.orderCode());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(1)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(1)).applyCoupon(aCouponCode);
        Mockito.verify(orderGateway, Mockito.times(1)).count();
        Mockito.verify(orderGateway, Mockito.times(1))
                .createInBatch(argThat(it -> it.size() == aItems.size()));
        Mockito.verify(orderDeliveryGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).create(argThat(it ->
                Objects.nonNull(it.getId())
                        && Objects.nonNull(it.getOrderCode())
                        && Objects.equals(it.getCustomerId(), aCustomerId)
                        && Objects.equals(it.getCouponCode().get(), aCouponCode)
                        && Objects.equals(it.getCouponPercentage(), aCoupon.getPercentage())
                        && Objects.equals(it.getOrderItems().size(), aItems.size())
                        && Objects.equals(it.getOrderStatus(), OrderStatus.WAITING_PAYMENT)
                        && Objects.nonNull(it.getOrderDeliveryId())
                        && Objects.nonNull(it.getOrderPaymentId())
                        && it.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0
                        && Objects.nonNull(it.getCreatedAt())
                        && Objects.nonNull(it.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandWithNullCouponCode_whenCallCreateOrderUseCase_thenShouldCreateOrder() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        final var aCustomerId = aCustomer.getAccountId();
        final String aCouponCode = null;
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("bola", BigDecimal.valueOf(200.0))));
        Mockito.when(orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(createOrderFreightDetails());
        Mockito.when(orderGateway.count())
                .thenReturn(1L);
        Mockito.when(orderGateway.createInBatch(Mockito.anySet()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderDeliveryGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderPaymentGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any()))
                .thenAnswer(it -> TransactionResult.success(it.getArgument(0, Supplier.class).get()));

        final var aOutput = this.createOrderUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.orderId());
        Assertions.assertNotNull(aOutput.orderCode());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(1)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(0)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).count();
        Mockito.verify(orderGateway, Mockito.times(1))
                .createInBatch(argThat(it -> it.size() == aItems.size()));
        Mockito.verify(orderDeliveryGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).create(argThat(it ->
                Objects.nonNull(it.getId())
                        && Objects.nonNull(it.getOrderCode())
                        && Objects.equals(it.getCustomerId(), aCustomerId)
                        && it.getCouponCode().isEmpty()
                        && Objects.equals(it.getCouponPercentage(), 0.0f)
                        && Objects.equals(it.getOrderItems().size(), aItems.size())
                        && Objects.equals(it.getOrderStatus(), OrderStatus.WAITING_PAYMENT)
                        && Objects.nonNull(it.getOrderDeliveryId())
                        && Objects.nonNull(it.getOrderPaymentId())
                        && it.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0
                        && Objects.nonNull(it.getCreatedAt())
                        && Objects.nonNull(it.getUpdatedAt())));
    }

    @Test
    void givenAValidCommandWithEmptyCouponCode_whenCallCreateOrderUseCase_thenShouldCreateOrder() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        final var aCustomerId = aCustomer.getAccountId();
        final String aCouponCode = "";
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("bola", BigDecimal.valueOf(200.0))));
        Mockito.when(orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(createOrderFreightDetails());
        Mockito.when(orderGateway.count())
                .thenReturn(1L);
        Mockito.when(orderGateway.createInBatch(Mockito.anySet()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderDeliveryGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderPaymentGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(orderGateway.create(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any()))
                .thenAnswer(it -> TransactionResult.success(it.getArgument(0, Supplier.class).get()));

        final var aOutput = this.createOrderUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.orderId());
        Assertions.assertNotNull(aOutput.orderCode());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(1)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(0)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).count();
        Mockito.verify(orderGateway, Mockito.times(1))
                .createInBatch(argThat(it -> it.size() == aItems.size()));
        Mockito.verify(orderDeliveryGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(1)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).create(argThat(it ->
                Objects.nonNull(it.getId())
                        && Objects.nonNull(it.getOrderCode())
                        && Objects.equals(it.getCustomerId(), aCustomerId)
                        && it.getCouponCode().isEmpty()
                        && Objects.equals(it.getCouponPercentage(), 0.0f)
                        && Objects.equals(it.getOrderItems().size(), aItems.size())
                        && Objects.equals(it.getOrderStatus(), OrderStatus.WAITING_PAYMENT)
                        && Objects.nonNull(it.getOrderDeliveryId())
                        && Objects.nonNull(it.getOrderPaymentId())
                        && it.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0
                        && Objects.nonNull(it.getCreatedAt())
                        && Objects.nonNull(it.getUpdatedAt())));
    }

    @Test
    void givenAValidCommand_whenCallCreateOrderUseCaseButTransactionFailure_shouldThrowTransactionFailureException() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = aCustomer.getAccountId();
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var expectedErrorMessage = "Error on create order";

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("bola", BigDecimal.valueOf(200.0))));
        Mockito.when(orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(createOrderFreightDetails());
        Mockito.when(orderCouponGateway.applyCoupon(aCouponCode))
                .thenReturn(createOrderCouponOutput(aCoupon));
        Mockito.when(orderGateway.count())
                .thenReturn(1L);
        Mockito.when(transactionManager.execute(Mockito.any()))
                .thenReturn(TransactionResult.failure(new Error(expectedErrorMessage)));

        final var aException = Assertions.assertThrows(TransactionFailureException.class,
                () -> this.createOrderUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(1)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(1)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).count();
        Mockito.verify(orderGateway, Mockito.times(0)).createInBatch(Mockito.any());
        Mockito.verify(orderDeliveryGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenInvalidCommand_whenCallCreateOrderUseCase_shouldReturnNotificationHandler() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = " ";
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = " ";
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("bola", BigDecimal.valueOf(200.0))));
        Mockito.when(orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(createOrderFreightDetails());
        Mockito.when(orderCouponGateway.applyCoupon(aCouponCode))
                .thenReturn(createOrderCouponOutput(aCoupon));
        Mockito.when(orderGateway.count())
                .thenReturn(1L);

        final var aOutput = this.createOrderUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertTrue(aOutput.hasError());
        Assertions.assertEquals(1, aOutput.getErrors().size());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(1)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(1)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(1)).count();
        Mockito.verify(orderGateway, Mockito.times(0)).createInBatch(Mockito.any());
        Mockito.verify(orderDeliveryGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenInvalidCustomerId_whenCallCreateOrderUseCase_shouldThrowNotFoundException() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = aCustomer.getAccountId();
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.createOrderUseCase.execute(aCommand));

        Assertions.assertNotNull(aException);
        Assertions.assertEquals(Fixture.notFoundMessage(Customer.class, aCustomerId), aException.getMessage());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(0)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(0)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(0)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).count();
        Mockito.verify(orderGateway, Mockito.times(0)).createInBatch(Mockito.any());
        Mockito.verify(orderDeliveryGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidAllSkus_whenCallCreateOrderUseCase_shouldThrowDomainException() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = aCustomer.getAccountId();
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "No product details found";

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createOrderUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(0)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(0)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).count();
        Mockito.verify(orderGateway, Mockito.times(0)).createInBatch(Mockito.any());
        Mockito.verify(orderDeliveryGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenInvalidOneSku_whenCallCreateOrderUseCase_shouldThrowDomainException() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        final var aCustomerId = aCustomer.getAccountId();
        final var aCouponCode = aCoupon.getCode().getValue();
        final var aFreightType = "SEDEX";
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 0;
        final var aItems = Set.of(
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "camiseta",
                        5
                ),
                CreateOrderItemsCommand.with(
                        IdUtils.generateWithoutDash(),
                        "bola",
                        50
                )
        );

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "No product details found";

        final var aCommand = CreateOrderCommand.with(
                aCustomerId,
                aCouponCode,
                aFreightType,
                aPaymentMethodId,
                aInstallments,
                aItems
        );

        Mockito.when(orderCustomerGateway.findByCustomerId(aCustomerId))
                .thenReturn(createOrderCustomerOutput(aCustomer));
        Mockito.when(orderProductGateway.getProductDetailsBySku(Mockito.any()))
                .thenReturn(Optional.of(createOrderProductDetailsOutput("camiseta", BigDecimal.valueOf(100.0))))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createOrderUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(orderCustomerGateway, Mockito.times(1)).findByCustomerId(aCustomerId);
        Mockito.verify(orderProductGateway, Mockito.times(2)).getProductDetailsBySku(Mockito.any());
        Mockito.verify(orderFreightGateway, Mockito.times(0)).calculateFreight(Mockito.any());
        Mockito.verify(orderCouponGateway, Mockito.times(0)).applyCoupon(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).count();
        Mockito.verify(orderGateway, Mockito.times(0)).createInBatch(Mockito.any());
        Mockito.verify(orderDeliveryGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderPaymentGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(orderGateway, Mockito.times(0)).create(Mockito.any());
    }

    private OrderCouponGateway.OrderCouponApplyOutput createOrderCouponOutput(final Coupon aCoupon) {
        return new OrderCouponGateway.OrderCouponApplyOutput(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue(),
                aCoupon.getPercentage()
        );
    }

    private Optional<OrderCustomerGateway.OrderCustomerOutput> createOrderCustomerOutput(final Customer aCustomer) {
        return Optional.of(
                new OrderCustomerGateway.OrderCustomerOutput(
                        aCustomer.getAccountId(),
                        aCustomer.getAddress().get().getZipCode(),
                        aCustomer.getAddress().get().getStreet(),
                        aCustomer.getAddress().get().getNumber(),
                        aCustomer.getAddress().get().getComplement(),
                        aCustomer.getAddress().get().getDistrict(),
                        aCustomer.getAddress().get().getCity(),
                        aCustomer.getAddress().get().getState()
                )
        );
    }

    private OrderFreightGateway.OrderFreightDetails createOrderFreightDetails() {
        return new OrderFreightGateway.OrderFreightDetails(
                "PAC",
                10.0f,
                5
        );
    }

    private OrderProductGateway.OrderProductDetails createOrderProductDetailsOutput(String sku, BigDecimal price) {
        return new OrderProductGateway.OrderProductDetails(
                sku,
                price,
                15.0,
                30.0,
                10.0,
                0.5
        );
    }
}
