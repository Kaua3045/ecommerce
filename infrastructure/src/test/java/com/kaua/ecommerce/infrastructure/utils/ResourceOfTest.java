package com.kaua.ecommerce.infrastructure.utils;

import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.infrastructure.exceptions.ImageSizeNotValidException;
import com.kaua.ecommerce.infrastructure.exceptions.ImageTypeNotValidException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Tag("unitTest")
public class ResourceOfTest {

    @Test
    void givenAValidMultipartFile_whenCallWith_thenReturnResource() throws IOException {
        final var mockPart = Mockito.mock(MultipartFile.class);

        Mockito.when(mockPart.getContentType()).thenReturn("image/jpeg");
        Mockito.when(mockPart.getSize()).thenReturn(600 * 1024L);
        Mockito.when(mockPart.getInputStream()).thenReturn(InputStream.nullInputStream());
        Mockito.when(mockPart.getBytes()).thenReturn(new byte[0]);
        Mockito.when(mockPart.getOriginalFilename()).thenReturn("image.jpg");

        final var aResult = ResourceOf.with(mockPart);

        Assertions.assertNotNull(aResult);
    }

    @Test
    void givenAnInvalidMultipartFile_whenCallWith_thenThrowDomainException() {
        final MultipartFile multipartFile = null;

        final var expectedErrorMessage = "'part' must not be null";

        final var aResult = Assertions.assertThrows(DomainException.class,
                () -> ResourceOf.with(multipartFile));

        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMultipartFile_whenCallWith_thenThrowImageTypeNotValidException() {
        final var mockPart = Mockito.mock(MultipartFile.class);

        Mockito.when(mockPart.getContentType()).thenReturn("image/gif");

        Assertions.assertThrows(
                ImageTypeNotValidException.class,
                () -> ResourceOf.with(mockPart)
        );
    }

    @Test
    void givenAnInvalidMultipartFile_whenCallWith_thenThrowImageSizeNotValidException() {
        final var mockPart = Mockito.mock(MultipartFile.class);

        Mockito.when(mockPart.getContentType()).thenReturn("image/jpeg");
        Mockito.when(mockPart.getSize()).thenReturn(600 * 1024L + 1);

        Assertions.assertThrows(
                ImageSizeNotValidException.class,
                () -> ResourceOf.with(mockPart)
        );
    }

    @Test
    void givenAnInvalidMultipartFileInputStream_whenCallWith_thenThrowRuntimeException() throws IOException {
        final var mockPart = Mockito.mock(MultipartFile.class);

        Mockito.when(mockPart.getContentType()).thenReturn("image/jpeg");
        Mockito.when(mockPart.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> ResourceOf.with(mockPart)
        );
    }
}
