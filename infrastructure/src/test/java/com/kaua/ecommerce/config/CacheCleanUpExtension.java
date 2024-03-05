package com.kaua.ecommerce.config;

import com.kaua.ecommerce.infrastructure.customer.address.persistence.AddressCacheEntityRepository;
import com.kaua.ecommerce.infrastructure.customer.persistence.CustomerCacheEntityRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class CacheCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(AddressCacheEntityRepository.class),
                appContext.getBean(CustomerCacheEntityRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
