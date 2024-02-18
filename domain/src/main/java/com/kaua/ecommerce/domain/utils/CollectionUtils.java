package com.kaua.ecommerce.domain.utils;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <IN, OUT> Set<OUT> mapTo(final Set<IN> aList, final Function<IN, OUT> mapper) {
        if (aList == null) {
            return Collections.emptySet();
        }

        return aList.stream().map(mapper).collect(Collectors.toSet());
    }
}
