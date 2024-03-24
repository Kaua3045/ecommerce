package com.kaua.ecommerce.application.usecases.freight.calculate;

import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.gateways.commands.CalculateFreightInput;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.freight.Freight;
import com.kaua.ecommerce.domain.freight.FreightType;
import com.kaua.ecommerce.domain.validation.Error;

import java.util.Objects;

public class DefaultCalculateFreightUseCase extends CalculateFreightUseCase {

    private final FreightGateway freightGateway;

    public DefaultCalculateFreightUseCase(final FreightGateway freightGateway) {
        this.freightGateway = Objects.requireNonNull(freightGateway);
    }

    @Override
    public Freight execute(CalculateFreightCommand input) {
        final var aFreightType = FreightType.of(input.type())
                .orElseThrow(() -> DomainException
                        .with(new Error("type %s was not found".formatted(input.type()))));

        CalculateFreightItemsCommand aItem = null;
        double aMaxVolume = 0;
        for (CalculateFreightItemsCommand item : input.items()) {
            final var aVolume = item.width() * item.height() * item.length();
            if (aVolume > aMaxVolume) {
                aMaxVolume = aVolume;
                aItem = item;
            }
        }

        if (aItem == null) {
            throw DomainException.with(new Error("No item found to calculate freight"));
        }

        return this.freightGateway.calculateFreight(CalculateFreightInput.with(
                input.zipCode(),
                aFreightType,
                aItem.height(),
                aItem.width(),
                aItem.length(),
                aItem.weight()
        ));
    }
}
