package com.kaua.ecommerce.application.usecases.order.create;

import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.either.Either;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.order.*;
import com.kaua.ecommerce.application.gateways.order.OrderCouponGateway.OrderCouponApplyOutput;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.order.*;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCreateOrderUseCase extends CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final OrderCouponGateway orderCouponGateway;
    private final OrderCustomerGateway orderCustomerGateway;
    private final OrderDeliveryGateway orderDeliveryGateway;
    private final OrderPaymentGateway orderPaymentGateway;
    private final OrderProductGateway orderProductGateway;
    private final OrderFreightGateway orderFreightGateway;
    private final TransactionManager transactionManager;

    public DefaultCreateOrderUseCase(
            final OrderGateway orderGateway,
            final OrderCouponGateway orderCouponGateway,
            final OrderCustomerGateway orderCustomerGateway,
            final OrderDeliveryGateway orderDeliveryGateway,
            final OrderPaymentGateway orderPaymentGateway,
            final OrderProductGateway orderProductGateway,
            final OrderFreightGateway orderFreightGateway,
            final TransactionManager transactionManager
    ) {
        this.orderGateway = Objects.requireNonNull(orderGateway);
        this.orderCouponGateway = Objects.requireNonNull(orderCouponGateway);
        this.orderCustomerGateway = Objects.requireNonNull(orderCustomerGateway);
        this.orderDeliveryGateway = Objects.requireNonNull(orderDeliveryGateway);
        this.orderPaymentGateway = Objects.requireNonNull(orderPaymentGateway);
        this.orderProductGateway = Objects.requireNonNull(orderProductGateway);
        this.orderFreightGateway = Objects.requireNonNull(orderFreightGateway);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    @Override
    public Either<NotificationHandler, CreateOrderOutput> execute(CreateOrderCommand input) {
        final var aNotification = NotificationHandler.create();

        // In microservices, findByCustomerId call an external service and possible failure point
        final var aCustomer = this.orderCustomerGateway.findByCustomerId(input.customerId())
                .orElseThrow(NotFoundException.with(Customer.class, input.customerId()));

        // In microservices, calculateFreight call an external service and possible failure point
        final var aOrderProductDetails = getOrderProductsDetails(input);
        final var aFreight = getCalculateFreight(aOrderProductDetails, aCustomer, input.freightType());

        final var aOrderDelivery = createOrderDelivery(aFreight, aCustomer);
        final var aOrderPayment = createOrderPayment(input);

        final var aOrderCode = OrderCode.create(this.orderGateway.count());
        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomer.customerId(),
                aOrderDelivery,
                aOrderPayment.getId()
        );
        addOrderItems(input, aOrderProductDetails, aOrder);
        aOrder.calculateTotalAmount(aOrderDelivery);

        // In microservices, applyCoupon call an external service and possible failure point
        final var aCoupon = input.getCouponCode().map(it -> applyCoupon(it, aOrder));

        aOrder.applyCoupon(
                aCoupon.map(OrderCouponApplyOutput::couponCode)
                        .orElse(null),
                aCoupon.map(OrderCouponApplyOutput::couponPercentage)
                        .orElse(0.0f));

        validateOrderComponents(aOrderDelivery, aNotification, aOrderPayment, aOrder);

        if (aNotification.hasError()) {
            return Either.left(aNotification);
        }

        final var aTransactionResult = this.transactionManager.execute(() -> {
            this.orderGateway.createInBatch(aOrder.getOrderItems());
            this.orderDeliveryGateway.create(aOrderDelivery);
            this.orderPaymentGateway.create(aOrderPayment);
            this.orderGateway.create(aOrder);
            return aOrder;
        });

        if (aTransactionResult.isFailure()) {
            throw TransactionFailureException.with(aTransactionResult.getErrorResult());
        }

        return Either.right(CreateOrderOutput.from(aOrder));
    }

    private OrderCouponApplyOutput applyCoupon(String aCouponCode, Order aOrder) {
        return this.orderCouponGateway.applyCoupon(aCouponCode, aOrder.getTotalAmount().floatValue());
    }

    private Set<OrderProductGateway.OrderProductDetails> getOrderProductsDetails(CreateOrderCommand input) {
        final var aResult = input.items().stream()
                .map(it -> this.orderProductGateway.getProductDetailsBySku(it.sku()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        if (aResult.isEmpty() || aResult.size() != input.items().size()) {
            throw DomainException.with(new Error("No product details found"));
        }

        return aResult;
    }

    private void addOrderItems(
            final CreateOrderCommand input,
            final Set<OrderProductGateway.OrderProductDetails> aOrderProductDetails,
            final Order aOrder
    ) {
        input.items().forEach(it -> {
            final var aDetail = aOrderProductDetails.stream()
                    .filter(product -> product.sku().equals(it.sku()))
                    .findFirst()
                    .get();

            final var aOrderItem = OrderItem.create(
                    aOrder.getId().getValue(),
                    it.productId(),
                    it.sku(),
                    it.quantity(),
                    aDetail.price()
            );

            aOrder.addItem(aOrderItem);
        });
    }

    private void validateOrderComponents(OrderDelivery aOrderDelivery, NotificationHandler aNotification, OrderPayment aOrderPayment, Order aOrder) {
        aOrderDelivery.validate(aNotification);
        aOrderPayment.validate(aNotification);
        aOrder.validate(aNotification);
    }

    private OrderFreightGateway.OrderFreightDetails getCalculateFreight(
            final Set<OrderProductGateway.OrderProductDetails> input,
            final OrderCustomerGateway.OrderCustomerOutput aCustomer,
            final String aFreightType
    ) {
        return this.orderFreightGateway.calculateFreight(new OrderFreightGateway.CalculateOrderFreightInput(
                aCustomer.zipCode(),
                aFreightType,
                input.stream()
                        .map(it -> new OrderFreightGateway.CalculateOrderFreightItemsInput(
                                it.weight(),
                                it.width(),
                                it.height(),
                                it.length()
                        ))
                        .collect(Collectors.toSet())
        ));
    }

    private OrderPayment createOrderPayment(final CreateOrderCommand input) {
        return OrderPayment.newOrderPayment(
                input.paymentMethodId(),
                input.installments()
        );
    }

    private OrderDelivery createOrderDelivery(final OrderFreightGateway.OrderFreightDetails aFreight, final OrderCustomerGateway.OrderCustomerOutput aCustomer) {
        return OrderDelivery.newOrderDelivery(
                aFreight.type(),
                aFreight.price(),
                aFreight.deadlineInDays(),
                aCustomer.street(),
                aCustomer.number(),
                aCustomer.complement(),
                aCustomer.district(),
                aCustomer.city(),
                aCustomer.state(),
                aCustomer.zipCode()
        );
    }
}
