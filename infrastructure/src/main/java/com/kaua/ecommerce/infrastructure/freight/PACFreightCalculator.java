package com.kaua.ecommerce.infrastructure.freight;

public class PACFreightCalculator implements FreightCalculator {

    // TODO: Implement call to correios api

    @Override
    public CalculatePriceAndDeadlineResponse calculate(
            final String cep,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new CalculatePriceAndDeadlineResponse(15.0F, 5);
    }
}
