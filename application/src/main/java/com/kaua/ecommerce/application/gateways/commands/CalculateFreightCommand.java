package com.kaua.ecommerce.application.gateways.commands;

import com.kaua.ecommerce.domain.freight.FreightType;

public record CalculateFreightCommand(
        String cep,
        FreightType type,
        double height,
        double width,
        double length,
        double weight
) {

    public static CalculateFreightCommand with(
            final String cep,
            final FreightType type,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new CalculateFreightCommand(cep, type, height, width, length, weight);
    }
}
