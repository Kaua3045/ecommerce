package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.category.persistence.CategoryJpaRepository;
import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressJpaRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerJpaRepository;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import com.kaua.ecommerce.infrastructure.outbox.OutboxEventRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductColorJpaRepository;
import com.kaua.ecommerce.infrastructure.product.persistence.ProductJpaRepository;
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
                appContext.getBean(ProductJpaRepository.class),
                appContext.getBean(ProductColorJpaRepository.class),
                appContext.getBean(CategoryJpaRepository.class),
                appContext.getBean(CustomerJpaRepository.class),
                appContext.getBean(AddressJpaRepository.class),
                appContext.getBean(InventoryJpaRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
