package com.kaua.ecommerce.application.gateways.order;

import java.util.Set;

public interface OrderFreightGateway {

    OrderFreightDetails calculateFreight(CalculateOrderFreightInput input);

    record CalculateOrderFreightInput(
            String zipCode,
            String type,
            Set<CalculateOrderFreightItemsInput> items
    ) { }

    record OrderFreightDetails(
            String type,
            float price,
            int deadlineInDays
    ) { }

    record CalculateOrderFreightItemsInput(
            double weight,
            double width,
            double height,
            double length
    ) { }
}
