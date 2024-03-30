package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.domain.order.OrderDelivery;

public record GetOrderByCodeDeliveryOutput(
        String orderDeliveryId,
        String freightType,
        float freightPrice,
        int deliveryEstimated,
        String street,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String zipCode
) {

    public static GetOrderByCodeDeliveryOutput from(final OrderDelivery aOrderDelivery) {
        return new GetOrderByCodeDeliveryOutput(
                aOrderDelivery.getId().getValue(),
                aOrderDelivery.getFreightType(),
                aOrderDelivery.getFreightPrice(),
                aOrderDelivery.getDeliveryEstimated(),
                aOrderDelivery.getStreet(),
                aOrderDelivery.getNumber(),
                aOrderDelivery.getComplement().orElse(null),
                aOrderDelivery.getDistrict(),
                aOrderDelivery.getCity(),
                aOrderDelivery.getState(),
                aOrderDelivery.getZipCode()
        );
    }
}
