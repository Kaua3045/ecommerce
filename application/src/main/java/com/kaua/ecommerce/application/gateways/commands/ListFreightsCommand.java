package com.kaua.ecommerce.application.gateways.commands;

public record ListFreightsCommand(
        String cep,
        double height,
        double width,
        double length,
        double weight
) {

    public static ListFreightsCommand with(
            final String cep,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new ListFreightsCommand(cep, height, width, length, weight);
    }
}
