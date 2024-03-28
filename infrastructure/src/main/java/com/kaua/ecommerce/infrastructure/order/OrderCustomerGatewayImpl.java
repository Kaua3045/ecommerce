package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderCustomerGateway;
import com.kaua.ecommerce.application.usecases.customer.retrieve.get.GetCustomerByAccountIdUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class OrderCustomerGatewayImpl implements OrderCustomerGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderCustomerGatewayImpl.class);

    private final GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase;

    public OrderCustomerGatewayImpl(final GetCustomerByAccountIdUseCase getCustomerByAccountIdUseCase) {
        this.getCustomerByAccountIdUseCase = Objects.requireNonNull(getCustomerByAccountIdUseCase);
    }

    @Override
    public Optional<OrderCustomerOutput> findByCustomerId(String customerId) {
        try {
            final var aCustomer = this.getCustomerByAccountIdUseCase.execute(customerId);
            return Optional.of(new OrderCustomerOutput(
                    aCustomer.accountId(),
                    aCustomer.address().zipCode(),
                    aCustomer.address().street(),
                    aCustomer.address().number(),
                    aCustomer.address().complement(),
                    aCustomer.address().district(),
                    aCustomer.address().city(),
                    aCustomer.address().state()
            ));
        } catch (final Exception e) {
            log.info("Error on get customer by account id: {}", customerId);
            return Optional.empty();
        }
    }
}
