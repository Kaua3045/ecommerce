package com.kaua.ecommerce.infrastructure.service.local;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.Resource;
import com.kaua.ecommerce.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        final var aKey = IdUtils.generate() + "-" + "COVER" + "-" +
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
        final var aKey = IdUtils.generate() + "-" + "COVER" + "-" +
                IdUtils.generate().replace("-", "")
                + "-" + aFileName;

        // when
        this.target.store(aKey, aResource);

        // when
        this.target.delete(aKey);

        // then
        Assertions.assertEquals(0, this.target.storage().size());
    }
}
