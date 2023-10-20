package com.kaua.ecommerce.application.either;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

public class EitherTest {

    @Test
    void testLeft() {
        Either<Integer, String> either = Either.left(42);
        Assertions.assertTrue(either.isLeft());
        Assertions.assertFalse(either.isRight());
        Assertions.assertEquals(42, either.getLeft());
        Assertions.assertThrows(NoSuchElementException.class, either::getRight);
    }

    @Test
    public void testRight() {
        Either<Integer, String> either = Either.right("Hello");
        Assertions.assertFalse(either.isLeft());
        Assertions.assertTrue(either.isRight());
        Assertions.assertEquals("Hello", either.getRight());
        Assertions.assertThrows(NoSuchElementException.class, either::getLeft);
    }

    @Test
    void testRightWithLeftMethods() {
        Either<Integer, String> either = Either.right("Hello");
        Assertions.assertFalse(either.isLeft());
        Assertions.assertTrue(either.isRight());
        Assertions.assertThrows(NoSuchElementException.class, either::getLeft);
        Assertions.assertDoesNotThrow(() -> {
            String value = either.getRight();
            Assertions.assertEquals("Hello", value);
        });
    }

    @Test
    void testLeftWithRightMethods() {
        Either<Integer, String> either = Either.left(42);
        Assertions.assertTrue(either.isLeft());
        Assertions.assertFalse(either.isRight());
        Assertions.assertThrows(NoSuchElementException.class, either::getRight);
        Assertions.assertDoesNotThrow(() -> {
            Integer value = either.getLeft();
            Assertions.assertEquals(42, value);
        });
    }

    @Test
    void testBaseMethods() {
        Either<Integer, String> either = new Either.BaseMethods<>();
        Assertions.assertThrows(UnsupportedOperationException.class, either::isLeft);
        Assertions.assertThrows(UnsupportedOperationException.class, either::isRight);
        Assertions.assertThrows(UnsupportedOperationException.class, either::getLeft);
        Assertions.assertThrows(UnsupportedOperationException.class, either::getRight);
    }
}
