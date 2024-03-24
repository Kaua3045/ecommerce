package com.kaua.ecommerce.infrastructure.utils;

import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.resource.Resource;
import com.kaua.ecommerce.domain.validation.Error;
import com.kaua.ecommerce.infrastructure.exceptions.ImageSizeNotValidException;
import com.kaua.ecommerce.infrastructure.exceptions.ImageTypeNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public final class ResourceOf {

    private ResourceOf() {
    }

    public static Resource with(final MultipartFile part) {
        if (part == null) {
            throw DomainException.with(new Error("'part' must not be null"));
        }

        isValidImage(part);

        try {
            return Resource.with(
                    part.getBytes(),
                    part.getContentType(),
                    part.getOriginalFilename()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void isValidImage(final MultipartFile part) {
        final var imageTypes = List.of(
                "image/jpeg",
                "image/jpg",
                "image/png"
        );

        final var invalidImageType = imageTypes.contains(part.getContentType());
        final var IMAGE_SIZE = 600;
        final var invalidImageSize = part.getSize() > IMAGE_SIZE * 1024;

        if (!invalidImageType) {
            throw new ImageTypeNotValidException();
        }

        if (invalidImageSize) {
            throw new ImageSizeNotValidException();
        }
    }
}
