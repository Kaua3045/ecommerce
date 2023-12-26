package com.kaua.ecommerce.domain.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class ResourceTest {

    @Test
    void givenAValidParams_whenCallWithResource_shouldInstantiate() {
        final var aInputStream = InputStream.nullInputStream();
        final var aContentType = "image/png";
        final var aFileName = "image.png";

        final var actualResource = Resource.with(aInputStream, aContentType, aFileName);

        Assertions.assertNotNull(actualResource);
        Assertions.assertEquals(aInputStream, actualResource.inputStream());
        Assertions.assertEquals(aContentType, actualResource.contentType());
        Assertions.assertEquals(aFileName, actualResource.fileName());
    }

    @Test
    void givenAnInvalidNullInputStream_whenCallWithResource_shouldThrowException() {
        final InputStream aInputStream = null;
        final var aContentType = "image/png";
        final var aFileName = "image.png";

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aInputStream, aContentType, aFileName)
        );
    }

    @Test
    void givenAnInvalidNullContentType_whenCallWithResource_shouldThrowException() {
        final var aInputStream = InputStream.nullInputStream();
        final String aContentType = null;
        final var aFileName = "image.png";

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aInputStream, aContentType, aFileName)
        );
    }

    @Test
    void givenAnInvalidNullFileName_whenCallWithResource_shouldThrowException() {
        final var aInputStream = InputStream.nullInputStream();
        final var aContentType = "image/png";
        final String aFileName = null;

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aInputStream, aContentType, aFileName)
        );
    }
}
