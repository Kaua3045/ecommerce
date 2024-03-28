package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderFreightGateway;
import com.kaua.ecommerce.application.usecases.freight.calculate.CalculateFreightCommand;
import com.kaua.ecommerce.application.usecases.freight.calculate.CalculateFreightItemsCommand;
import com.kaua.ecommerce.application.usecases.freight.calculate.CalculateFreightUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderFreightGatewayImpl implements OrderFreightGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderFreightGatewayImpl.class);

    private final CalculateFreightUseCase calculateFreightUseCase;

    public OrderFreightGatewayImpl(final CalculateFreightUseCase calculateFreightUseCase) {
        this.calculateFreightUseCase = Objects.requireNonNull(calculateFreightUseCase);
    }

    @Override
    public OrderFreightDetails calculateFreight(CalculateOrderFreightInput input) {
        try {
            final var aFreight = this.calculateFreightUseCase.execute(CalculateFreightCommand.with(
                    input.zipCode(),
                    input.type(),
                    input.items().stream().map(item -> CalculateFreightItemsCommand.with(
                            item.height(),
                            item.width(),
                            item.length(),
                            item.weight()
                    )).collect(Collectors.toSet()))
            );
            return new OrderFreightDetails(
                    aFreight.getType().name(),
                    aFreight.getPrice(),
                    aFreight.getDeadline()
            );
        } catch (final Exception e) {
            log.error("Error on calculate freight: {}", input);
            throw e;
        }
    }
}
