package com.kaua.ecommerce.domain.pagination;

import com.kaua.ecommerce.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public record Period(
        String startDate,
        String endDate
) {

    public Optional<Instant> startDateInstant() {
        return startDate == null || startDate.isBlank()
                ? Optional.empty()
                : Optional.of(InstantUtils.parse(startDate));
    }

    public Optional<Instant> endDateInstant() {
        return endDate == null || endDate.isBlank()
                ? Optional.empty()
                : Optional.of(InstantUtils.parse(endDate));
    }
}
