package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderProductGateway;
import com.kaua.ecommerce.application.usecases.product.retrieve.details.GetProductDetailsBySkuUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class OrderProductGatewayImpl implements OrderProductGateway {

    private static final Logger log = LoggerFactory.getLogger(OrderProductGatewayImpl.class);

    private final GetProductDetailsBySkuUseCase getProductDetailsBySkuUseCase;

    public OrderProductGatewayImpl(final GetProductDetailsBySkuUseCase getProductDetailsBySkuUseCase) {
        this.getProductDetailsBySkuUseCase = Objects.requireNonNull(getProductDetailsBySkuUseCase);
    }

    @Override
    public Optional<OrderProductDetails> getProductDetailsBySku(String sku) {
        try {
            final var aProductDetails = this.getProductDetailsBySkuUseCase.execute(sku);
            return Optional.ofNullable(aProductDetails)
                    .map(productDetail -> new OrderProductDetails(
                            productDetail.sku(),
                            productDetail.price(),
                            productDetail.weight(),
                            productDetail.width(),
                            productDetail.height(),
                            productDetail.length()
                    ));
        } catch (final Exception e) {
            log.error("Error on get product details by sku: {}", sku);
            return Optional.empty();
        }
    }
}
