package com.kaua.ecommerce.application.usecases.freight.calculate;

public record CalculateFreightItemsCommand(
        double weight,
        double width,
        double height,
        double length
) {

    public  static CalculateFreightItemsCommand with(
            final double weight,
            final double width,
            final double height,
            final double length
    ) {
        return new CalculateFreightItemsCommand(weight, width, height, length);
    }
}
