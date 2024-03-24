package com.kaua.ecommerce.infrastructure.freight;

import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.gateways.commands.CalculateFreightInput;
import com.kaua.ecommerce.application.gateways.commands.ListFreightsInput;
import com.kaua.ecommerce.domain.freight.Freight;
import com.kaua.ecommerce.domain.freight.FreightType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FreightGatewayImpl implements FreightGateway {

    @Override
    public Freight calculateFreight(CalculateFreightInput aCommand) {
        final var aFreightCalculator = FreightCalculatorFactory.create(aCommand.type());
        final var aFreightResponse = aFreightCalculator.calculate(
                aCommand.cep(),
                aCommand.height(),
                aCommand.width(),
                aCommand.length(),
                aCommand.weight()
        );
        return Freight.newFreight(
                aCommand.cep(),
                aCommand.type(),
                aFreightResponse.price(),
                aFreightResponse.deadline());
    }

    @Override
    public List<Freight> listFreights(ListFreightsInput aCommand) {
        final List<Freight> aFreights = new ArrayList<>();

        for (final var aType : FreightType.values()) {
            if (!aType.equals(FreightType.UNKNOWN)) {
                final var aFreightCalculator = FreightCalculatorFactory.create(aType);
                final var aFreightResponse = aFreightCalculator.calculate(
                        aCommand.cep(),
                        aCommand.height(),
                        aCommand.width(),
                        aCommand.length(),
                        aCommand.weight()
                );
                aFreights.add(Freight.newFreight(
                        aCommand.cep(),
                        aType,
                        aFreightResponse.price(),
                        aFreightResponse.deadline()));
            }
        }
        return aFreights;
    }
}
