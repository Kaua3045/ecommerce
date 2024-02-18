package com.kaua.ecommerce.domain.utils;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.product.ProductID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CollectionUtilsTest extends UnitTest {

    @Test
    void givenNullList_whenMapTo_thenEmptyList() {
        // Arrange
        final Set<ProductID> list = null;

        // Act
        var aResult = CollectionUtils.mapTo(list, ProductID::getValue);

        // Assert
        Assertions.assertTrue(aResult.isEmpty());
    }

    @Test
    void givenList_whenMapTo_thenMappedList() {
        // Arrange
        final Set<ProductID> list = Set.of(ProductID.unique(), ProductID.unique());

        // Act
        var aResult = CollectionUtils.mapTo(list, ProductID::getValue);

        // Assert
        Assertions.assertEquals(list.size(), aResult.size());
    }
}
