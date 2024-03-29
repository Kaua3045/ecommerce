package com.kaua.ecommerce.config;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class ConcurrentlyTestHelper {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentlyTestHelper.class);

    private ConcurrentlyTestHelper() {
    }

    public static void doSyncAndConcurrently(
            final int threadCount,
            final Consumer<String> operation,
            final String classTestedName,
            final int successCount,
            final int errorCount
    ) throws InterruptedException {
        final var startLatch = new CountDownLatch(1);
        final var endLatch = new CountDownLatch(threadCount);
        final var aSuccessCount = new AtomicInteger(0);
        final var aErrorCount = new AtomicInteger(0);

        if (threadCount <= 0 || threadCount > 10) {
            throw new RuntimeException("Thread count must be between 1 and 10");
        }

        final var aThreadPrefix = classTestedName + "-";
        final var aExecutorService = Executors.newFixedThreadPool(threadCount, new CustomizableThreadFactory(
                aThreadPrefix
        ));

        for (int i = 0; i < threadCount; i++) {
            String threadName = "Thread " + i;
            aExecutorService.execute(() -> {
                try {
                    startLatch.await();
                    operation.accept(threadName);
                    aSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    aErrorCount.incrementAndGet();
                    log.info("Error on execute test in class {} [{}]: {}", classTestedName, threadName, e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        aExecutorService.shutdown();

        Assertions.assertEquals(successCount, aSuccessCount.get());
        Assertions.assertEquals(errorCount, aErrorCount.get());
    }
}
