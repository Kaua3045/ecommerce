package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class OrderProductGatewayImplTest {

    @Autowired
    private OrderProductGatewayImpl orderProductGatewayImpl;

    @Test
    void testReturnEmptyOptionalWhenGetProductDetailsBySku() {
        Assertions.assertTrue(orderProductGatewayImpl.getProductDetailsBySku("sku-123").isEmpty());
    }
}
