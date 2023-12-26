package com.kaua.ecommerce.domain.product;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class ProductID extends Identifier {

    private final String value;

    public ProductID(String value) {
        this.value = Objects.requireNonNull(value, "'value' should not be null");
    }

    public static ProductID unique() {
        return new ProductID(IdUtils.generate());
    }

    public static ProductID from(final String value) {
        return new ProductID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductID productID = (ProductID) o;
        return Objects.equals(getValue(), productID.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
