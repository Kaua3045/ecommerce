package com.kaua.ecommerce.infrastructure.utils;

import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtils {

    private SpecificationUtils() {
    }

    public static <T> Specification<T> like(final String property, final String value) {
        return (root, query, cb) -> cb.like(cb.upper(root.get(property)), "%" + value.toUpperCase() + "%");
    }

    public static <T> Specification<T> whereEqual(final String property, final String value) {
        return (root, query, cb) -> cb.equal(root.get(property), value);
    }

    public static <T> Specification<T> whereEqual(final String property, final Enum<?> value) {
        return (root, query, cb) -> cb.equal(root.get(property), value);
    }
}
