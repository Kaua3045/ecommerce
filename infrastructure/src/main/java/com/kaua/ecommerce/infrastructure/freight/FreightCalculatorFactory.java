package com.kaua.ecommerce.infrastructure.freight;

import com.kaua.ecommerce.domain.freight.FreightType;

public final class FreightCalculatorFactory {

    private FreightCalculatorFactory() {
    }

    public static FreightCalculator create(final FreightType aType) {
        return switch (aType) {
            case PAC -> new PACFreightCalculator();
            case SEDEX -> new SedexFreightCalculator();
            default -> throw new IllegalArgumentException("Unknown type: " + aType);
        };
    }
}
