package com.kaua.ecommerce.infrastructure.order.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderCommand;

import java.util.List;
import java.util.stream.Collectors;

public record CreateOrderInput(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("coupon_code") String couponCode,
        @JsonProperty("freight_type") String freightType,
        @JsonProperty("payment_method_id") String paymentMethodId,
        @JsonProperty("installments") int installments,
        @JsonProperty("items") List<CreateOrderItemInput> items
) {

    public CreateOrderCommand toCommand() {
        return CreateOrderCommand.with(
                customerId,
                couponCode,
                freightType,
                paymentMethodId,
                installments,
                items.stream().map(CreateOrderItemInput::toCommand).collect(Collectors.toSet())
        );
    }
}
