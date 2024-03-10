package com.kaua.ecommerce.domain.pagination;

import com.kaua.ecommerce.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PeriodTest extends UnitTest {

    @Test
    void givenAValidStartDate_whenCallStartDateInstant_shouldReturnAnOptionalInstant() {
        final var startDate = "2021-01-01T00:00:00Z";
        final String endDate = null;

        final var period = new Period(startDate, endDate);

        final var startDateInstant = period.startDateInstant();

        Assertions.assertTrue(startDateInstant.isPresent());
    }

    @Test
    void givenAValidEndDate_whenCallEndDateInstant_shouldReturnAnOptionalInstant() {
        final String startDate = null;
        final var endDate = "2021-01-31T23:59:59Z";

        final var period = new Period(startDate, endDate);

        final var endDateInstant = period.endDateInstant();

        Assertions.assertTrue(endDateInstant.isPresent());
    }

    @Test
    void givenAnInvalidStartDate_whenCallStartDateInstant_shouldReturnAnEmptyOptionalInstant() {
        final var startDate = " ";
        final String endDate = "2021-01-31T23:59:59Z";

        final var period = new Period(startDate, endDate);

        final var startDateInstant = period.startDateInstant();

        Assertions.assertTrue(startDateInstant.isEmpty());
        Assertions.assertTrue(period.endDateInstant().isPresent());
    }

    @Test
    void givenAnInvalidEndDate_whenCallEndDateInstant_shouldReturnAnEmptyOptionalInstant() {
        final String startDate = "2021-01-01T00:00:00Z";
        final var endDate = " ";

        final var period = new Period(startDate, endDate);

        final var endDateInstant = period.endDateInstant();

        Assertions.assertTrue(endDateInstant.isEmpty());
        Assertions.assertTrue(period.startDateInstant().isPresent());
    }

    @Test
    void givenAnInvalidNullStartDate_whenCallStartDateInstant_shouldReturnAnEmptyOptionalInstant() {
        final String startDate = null;
        final var endDate = "2021-01-31T23:59:59Z";

        final var period = new Period(startDate, endDate);

        final var startDateInstant = period.startDateInstant();

        Assertions.assertTrue(startDateInstant.isEmpty());
        Assertions.assertTrue(period.endDateInstant().isPresent());
    }

    @Test
    void givenAnInvalidNullEndDate_whenCallEndDateInstant_shouldReturnAnEmptyOptionalInstant() {
        final var startDate = "2021-01-01T00:00:00Z";
        final String endDate = null;

        final var period = new Period(startDate, endDate);

        final var endDateInstant = period.endDateInstant();

        Assertions.assertTrue(endDateInstant.isEmpty());
        Assertions.assertTrue(period.startDateInstant().isPresent());
    }
}
