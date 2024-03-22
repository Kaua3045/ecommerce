package com.kaua.ecommerce.domain.resource;

import com.kaua.ecommerce.domain.resource.Resource;
import com.kaua.ecommerce.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceTest {

    @Test
    void givenAValidParams_whenCallWithResource_shouldInstantiate() {
        final var aContent = "content".getBytes();
        final var aContentType = "image/png";
        final var aFileName = "image.png";

        final var actualResource = Resource.with(aContent, aContentType, aFileName);

        Assertions.assertNotNull(actualResource);
        Assertions.assertEquals(aContent, actualResource.content());
        Assertions.assertEquals(aContentType, actualResource.contentType());
        Assertions.assertEquals(aFileName, actualResource.fileName());
    }

    @Test
    void givenAValidParamsButFilenameLengthMoreThan20_whenCallWithResource_shouldInstantiate() {
        final var aContent = "content".getBytes();
        final var aContentType = "image/png";
        final var aFilenameWithoutExtension = RandomStringUtils.generateValue(64);
        final var aFileName = aFilenameWithoutExtension + ".png";
        final var aFilenameAfterTruncate = aFilenameWithoutExtension.substring(0, 20) + ".png";

        final var actualResource = Resource.with(aContent, aContentType, aFileName);

        Assertions.assertNotNull(actualResource);
        Assertions.assertEquals(aContent, actualResource.content());
        Assertions.assertEquals(aContentType, actualResource.contentType());
        Assertions.assertEquals(aFilenameAfterTruncate, actualResource.fileName());
    }

    @Test
    void givenAnInvalidNullContent_whenCallWithResource_shouldThrowException() {
        final byte[] aContent = null;
        final var aContentType = "image/png";
        final var aFileName = "image.png";

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aContent, aContentType, aFileName)
        );
    }

    @Test
    void givenAnInvalidNullContentType_whenCallWithResource_shouldThrowException() {
        final var aContent = "content".getBytes();
        final String aContentType = null;
        final var aFileName = "image.png";

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aContent, aContentType, aFileName)
        );
    }

    @Test
    void givenAnInvalidNullFileName_whenCallWithResource_shouldThrowException() {
        final var aContent = "content".getBytes();
        final var aContentType = "image/png";
        final String aFileName = null;

        Assertions.assertThrows(
                NullPointerException.class,
                () -> Resource.with(aContent, aContentType, aFileName)
        );
    }
}
