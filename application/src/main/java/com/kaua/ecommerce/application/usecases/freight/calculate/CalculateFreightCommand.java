package com.kaua.ecommerce.application.usecases.freight.calculate;

import java.util.Set;

public record CalculateFreightCommand(
        String zipCode,
        String type,
        Set<CalculateFreightItemsCommand> items
) {

    public static CalculateFreightCommand with(
            final String zipCode,
            final String type,
            final Set<CalculateFreightItemsCommand> items
    ) {
        return new CalculateFreightCommand(zipCode, type, items);
    }
}
