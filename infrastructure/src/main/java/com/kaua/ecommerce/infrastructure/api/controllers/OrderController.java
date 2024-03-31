package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.order.create.CreateOrderUseCase;
import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeOutput;
import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeUseCase;
import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.infrastructure.api.OrderAPI;
import com.kaua.ecommerce.infrastructure.order.models.CreateOrderInput;
import com.kaua.ecommerce.infrastructure.utils.LogControllerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class OrderController implements OrderAPI {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByCodeUseCase getOrderByCodeUseCase;

    public OrderController(
            final CreateOrderUseCase createOrderUseCase,
            final GetOrderByCodeUseCase getOrderByCodeUseCase
    ) {
        this.createOrderUseCase = Objects.requireNonNull(createOrderUseCase);
        this.getOrderByCodeUseCase = Objects.requireNonNull(getOrderByCodeUseCase);
    }

    @Override
    public ResponseEntity<?> createOrder(CreateOrderInput body) {
        // TODO: In the future, send body to a topic in Kafka and return 201 to the client, receive in kafka and process the order in async
        final var aCommand = body.toCommand();

        final var aResult = this.createOrderUseCase.execute(aCommand);

        LogControllerResult.logResult(
                log,
                Order.class,
                "createOrder",
                aResult
        );

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public GetOrderByCodeOutput getOrderByCode(String code) {
        return this.getOrderByCodeUseCase.execute(code);
    }
}
