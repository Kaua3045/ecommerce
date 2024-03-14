package com.kaua.ecommerce.infrastructure.freight;

public interface FreightCalculator {

    CalculatePriceAndDeadlineResponse calculate(String cep, double height, double width, double length, double weight);

    record CalculatePriceAndDeadlineResponse(float price, int deadline) {
    }
}
