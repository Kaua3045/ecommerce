package com.kaua.ecommerce.domain.category;

import com.kaua.ecommerce.domain.Identifier;
import com.kaua.ecommerce.domain.utils.IdUtils;

import java.util.Objects;

public class CategoryID extends Identifier {

    private final String value;

    private CategoryID(final String value) {
        this.value = Objects.requireNonNull(value, "'value' should not be null");
    }

    public static CategoryID unique() {
        return new CategoryID(IdUtils.generate());
    }

    public static CategoryID from(final String value) {
        return new CategoryID(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CategoryID that = (CategoryID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
