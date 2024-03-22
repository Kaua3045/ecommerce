package com.kaua.ecommerce.application.gateways.commands;

public record ListFreightsInput(
        String cep,
        double height,
        double width,
        double length,
        double weight
) {

    public static ListFreightsInput with(
            final String cep,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new ListFreightsInput(cep, height, width, length, weight);
    }
}
