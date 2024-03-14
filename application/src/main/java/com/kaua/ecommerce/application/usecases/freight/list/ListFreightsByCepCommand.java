package com.kaua.ecommerce.application.usecases.freight.list;

public record ListFreightsByCepCommand(
        String cep,
        double height,
        double width,
        double length,
        double weight
) {

    public static ListFreightsByCepCommand with(
            final String cep,
            final double height,
            final double width,
            final double length,
            final double weight
    ) {
        return new ListFreightsByCepCommand(cep, height, width, length, weight);
    }
}
