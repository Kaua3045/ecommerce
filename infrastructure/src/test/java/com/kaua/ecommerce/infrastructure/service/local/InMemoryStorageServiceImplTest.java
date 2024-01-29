package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.Resource;
import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@UnitTest
public class InMemoryStorageServiceImplTest {

    private InMemoryStorageServiceImpl target = new InMemoryStorageServiceImpl();

    @BeforeEach
    void cleanUp() {
        this.target.clear();
    }

    @Test
    void givenValidResource_whenCallStore_thenShouldUploadFile() {
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
        this.target.store(aKey, aResource);

        // then
        Assertions.assertEquals(1, this.target.storage().size());
    }

    @Test
    void givenValidLocation_whenCallDelete_thenShouldDeleteFile() {
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
        this.target.store(aKey, aResource);

        // when
        this.target.delete(aKey);

        // then
        Assertions.assertEquals(0, this.target.storage().size());
    }

    @Test
    void givenValidLocations_whenCallDeleteAllByLocation_thenShouldDeleteFiles() {
        // given
        final var aFileNameOne = "avatar.png";
        final var aResourceOne = Resource.with(
                "content".getBytes(),
                "image/png",
                aFileNameOne
        );
        final var aKeyOne = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + aFileNameOne;

        final var aFileNameTwo = "product.png";
        final var aResourceTwo = Resource.with(
                "content".getBytes(),
                "image/png",
                aFileNameTwo
        );
        final var aKeyTwo = IdUtils.generate() + "-" + "BANNER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + aFileNameTwo;

        // when
        this.target.store(aKeyOne, aResourceOne);
        this.target.store(aKeyTwo, aResourceTwo);

        // when
        Assertions.assertEquals(2, this.target.storage().size());

        this.target.deleteAllByLocation(List.of(aKeyOne, aKeyTwo));

        // then
        Assertions.assertEquals(0, this.target.storage().size());
    }
}
