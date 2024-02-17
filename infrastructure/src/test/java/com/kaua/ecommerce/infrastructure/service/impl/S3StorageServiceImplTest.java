package com.kaua.ecommerce.infrastructure.service.impl;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.Resource;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@IntegrationTest
public class S3StorageServiceImplTest {

    private S3StorageServiceImpl target;

    private S3Client s3Client;

    private String bucketName = "test";

    @BeforeEach
    void setUp() {
        this.s3Client = Mockito.mock(S3Client.class);

        this.target = new S3StorageServiceImpl(this.bucketName, s3Client);
    }

    @Test
    void givenAValidResource_whenCallStore_thenShouldUploadFile() {
        // given
        final var aFileName = "avatar.png";
        final var aResource = Resource.with(
                "content".getBytes(),
                "image/png",
                aFileName
        );
        final var aKey = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + aFileName;

        // when
        Mockito.doReturn(buildPutObjectResponse())
                .when(s3Client)
                .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));

        this.target.store(aKey, aResource);

        // then
        final var captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Mockito.verify(s3Client, Mockito.times(1)).putObject(captor.capture(), Mockito.any(RequestBody.class));
    }

    @Test
    void givenAValidResourceButS3Throws_whenCallStore_thenShouldThrowRuntimeException() {
        // given
        final var aFileName = "avatar.png";
        final var aResource = Resource.with(
                "content".getBytes(),
                "image/png",
                aFileName
        );
        final var aKey = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + aFileName;

        // when
        Mockito.doThrow(S3Exception.class)
                .when(s3Client)
                .putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));

        Assertions.assertThrows(RuntimeException.class, () -> this.target.store(aKey, aResource));

        // then
        final var captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Mockito.verify(s3Client, Mockito.times(1)).putObject(captor.capture(), Mockito.any(RequestBody.class));
    }

    @Test
    void givenAValidLocation_whenCallDelete_thenShouldDeleteFile() {
        // given
        final var aLocation = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "product.png";

        // when
        Mockito.when(s3Client.deleteObject(Mockito.any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        Assertions.assertDoesNotThrow(() -> this.target.delete(aLocation));

        // then
        Mockito.verify(s3Client, Mockito.times(1)).deleteObject(Mockito.any(DeleteObjectRequest.class));
    }

    @Test
    void givenAValidLocationButS3Throws_whenCallDelete_thenShouldThrowRuntimeException() {
        // given
        final var aLocation = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "product.png";
        final var expectedErrorMessage = "software.amazon.awssdk.services.s3.model.S3Exception";

        // when
        Mockito.when(s3Client.deleteObject(Mockito.any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.class);

        final var aResult = Assertions.assertThrows(RuntimeException.class,
                () -> this.target.delete(aLocation));

        // then
        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(s3Client, Mockito.times(1)).deleteObject(Mockito.any(DeleteObjectRequest.class));
    }

    @Test
    void givenAValidLocations_whenCallDeleteAllByLocation_thenShouldDeleteFiles() {
        // given
        final var aLocationOne = IdUtils.generate() + "-" + "GALLERY" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "product.png";
        final var aLocationTwo = IdUtils.generate() + "-" + "GALLERY" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "products.png";

        // when
        Mockito.when(s3Client.deleteObjects(Mockito.any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        Assertions.assertDoesNotThrow(() -> this.target
                .deleteAllByLocation(List.of(aLocationOne, aLocationTwo)));

        // then
        Mockito.verify(s3Client, Mockito.times(1)).deleteObjects(Mockito.any(DeleteObjectsRequest.class));
    }

    @Test
    void givenAValidLocationsButS3Throws_whenCallDeleteAllByLocation_thenShouldThrowRuntimeException() {
        // given
        final var aLocationOne = IdUtils.generate() + "-" + "GALLERY" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "product.png";
        final var aLocationTwo = IdUtils.generate() + "-" + "GALLERY" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + "products.png";

        final var aLocations = List.of(aLocationOne, aLocationTwo);

        final var expectedErrorMessage = "software.amazon.awssdk.services.s3.model.S3Exception";

        // when
        Mockito.when(s3Client.deleteObjects(Mockito.any(DeleteObjectsRequest.class)))
                .thenThrow(S3Exception.class);

        final var aResult = Assertions.assertThrows(RuntimeException.class,
                () -> this.target.deleteAllByLocation(aLocations));

        // then
        Assertions.assertEquals(expectedErrorMessage, aResult.getMessage());

        Mockito.verify(s3Client, Mockito.times(1)).deleteObjects(Mockito.any(DeleteObjectsRequest.class));
    }

    @Test
    void givenAnEmptyLocations_whenCallDeleteAllByLocation_thenShouldReturn() {
        // when
        Assertions.assertDoesNotThrow(() -> this.target
                .deleteAllByLocation(List.of()));

        // then
        Mockito.verify(s3Client, Mockito.times(0)).deleteObjects(Mockito.any(DeleteObjectsRequest.class));
    }

    private PutObjectResponse buildPutObjectResponse() {
        return PutObjectResponse.builder()
                .eTag("test")
                .build();
    }
}
