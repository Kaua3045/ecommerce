package com.kaua.ecommerce.application.order.create;

import com.kaua.ecommerce.application.gateways.order.OrderCustomerGateway;
import com.kaua.ecommerce.application.gateways.order.OrderFreightGateway;
import com.kaua.ecommerce.application.gateways.order.OrderProductGateway;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderCommand;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderItemsCommand;
import com.kaua.ecommerce.application.usecases.order.create.CreateOrderUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@IntegrationTest
public class CreateOrderUseCaseIT {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    @Autowired
    private OrderJpaEntityRepository orderJpaEntityRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    @MockBean
    private OrderCustomerGateway orderCustomerGateway;

    @MockBean
    private OrderFreightGateway orderFreightGateway;

    @MockBean
    private OrderProductGateway orderProductGateway;

    @Test
    void testConcurrencyOnCallCreateOrderUseCase() {
        final var aCustomer = Fixture.Customers.customerWithAllParams;

        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aCouponSlotOne = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaEntityRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlotOne)
        ));

        final var aCouponCode = aCoupon.getCode().getValue();
        final var aCustomerId = aCustomer.getAccountId();
        final var aFreightType = "PAC";
        final var aPaymentMethodId = "payment-method-id";
        final var aPaymentInstallments = 0;
        final var aSkuCamiseta = Fixture.createSku("camiseta");
        final var aOrderItems = Set.of(
                CreateOrderItemsCommand.with(
                        "product-id",
                        aSkuCamiseta,
                        10
                ));

        final var aExecutions = 5;
        final var aCountSuccessExecutionAtomic = new AtomicInteger(0);
        final var aCountErrorExecutionAtomic = new AtomicInteger(0);
        final var aExpectedSuccessExecution = 1;
        final var aExpectedErrorExecution = 4;

        Mockito.when(this.orderCustomerGateway.findByCustomerId(Mockito.anyString()))
                .thenReturn(Optional.of(new OrderCustomerGateway.OrderCustomerOutput(
                        aCustomer.getAccountId(),
                        aCustomer.getAddress().get().getZipCode(),
                        aCustomer.getAddress().get().getStreet(),
                        aCustomer.getAddress().get().getNumber(),
                        aCustomer.getAddress().get().getComplement(),
                        aCustomer.getAddress().get().getDistrict(),
                        aCustomer.getAddress().get().getCity(),
                        aCustomer.getAddress().get().getState()
                ))).getMock();

        Mockito.when(this.orderProductGateway.getProductDetailsBySku(Mockito.anyString()))
                .thenReturn(Optional.of(new OrderProductGateway.OrderProductDetails(
                        aSkuCamiseta,
                        BigDecimal.valueOf(100.0),
                        10.0,
                        10.0,
                        10.0,
                        10.0
                )));

        Mockito.when(this.orderFreightGateway.calculateFreight(Mockito.any()))
                .thenReturn(new OrderFreightGateway.OrderFreightDetails(
                        aFreightType,
                        10.0f,
                        5
                ));

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(1, this.couponSlotJpaEntityRepository.count());

        ExecutorService executorService = Executors.newFixedThreadPool(aExecutions);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < aExecutions; i++) {
            tasks.add(() -> {
                try {
                    this.createOrderUseCase.execute(
                            CreateOrderCommand.with(
                                    aCustomerId,
                                    aCouponCode,
                                    aFreightType,
                                    aPaymentMethodId,
                                    aPaymentInstallments,
                                    aOrderItems
                            )
                    );
                    aCountSuccessExecutionAtomic.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    aCountErrorExecutionAtomic.incrementAndGet();
                }
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
        Assertions.assertEquals(1, this.orderJpaEntityRepository.count());
        Assertions.assertEquals(1, this.orderItemJpaEntityRepository.count());
        Assertions.assertEquals(aExpectedSuccessExecution, aCountSuccessExecutionAtomic.get());
        Assertions.assertEquals(aExpectedErrorExecution, aCountErrorExecutionAtomic.get());
    }
}
