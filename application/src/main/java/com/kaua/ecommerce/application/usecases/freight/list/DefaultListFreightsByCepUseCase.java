package com.kaua.ecommerce.application.usecases.freight.list;

import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.gateways.commands.ListFreightsInput;
import com.kaua.ecommerce.domain.freight.Freight;

import java.util.List;
import java.util.Objects;

public class DefaultListFreightsByCepUseCase extends ListFreightsByCepUseCase {

    private final FreightGateway freightGateway;

    public DefaultListFreightsByCepUseCase(final FreightGateway freightGateway) {
        this.freightGateway = Objects.requireNonNull(freightGateway);
    }

    @Override
    public List<Freight> execute(ListFreightsByCepCommand input) {
        final var aListFreightsCommand = ListFreightsInput.with(
                input.cep(),
                input.height(),
                input.width(),
                input.length(),
                input.weight()
        );

        return this.freightGateway.listFreights(aListFreightsCommand);
    }
}
