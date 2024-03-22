package com.kaua.ecommerce.application.gateways.commands;

import com.kaua.ecommerce.domain.freight.FreightType;

public record CalculateFreightInput(
        String cep,
        FreightType type,
        double height,
        double width,
        double length,
        double weight
) {

    public static CalculateFreightInput with(
            final String cep,
            final FreightType type,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new CalculateFreightInput(cep, type, height, width, length, weight);
    }
}
