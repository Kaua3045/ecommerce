package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.inventory.movement.persistence.InventoryMovementJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderItemJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaEntityRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class JpaCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(OutboxEventRepository.class),
                appContext.getBean(ProductJpaEntityRepository.class),
                appContext.getBean(ProductColorJpaEntityRepository.class),
                appContext.getBean(CategoryJpaEntityRepository.class),
                appContext.getBean(CustomerJpaEntityRepository.class),
                appContext.getBean(AddressJpaEntityRepository.class),
                appContext.getBean(InventoryJpaEntityRepository.class),
                appContext.getBean(InventoryMovementJpaEntityRepository.class),
                appContext.getBean(CouponSlotJpaEntityRepository.class),
                appContext.getBean(CouponJpaEntityRepository.class),
                appContext.getBean(OrderItemJpaEntityRepository.class),
                appContext.getBean(OrderDeliveryJpaEntityRepository.class),
                appContext.getBean(OrderPaymentJpaEntityRepository.class),
                appContext.getBean(OrderJpaEntityRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
