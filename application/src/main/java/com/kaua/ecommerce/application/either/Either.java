package com.kaua.ecommerce.application.either;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> {

    public static <L, R> Either<L, R> right(R value) {
        return new BaseMethods<L, R>().new Right(value);
    }

    public static <L, R> Either<L, R> left(L value) {
        return new BaseMethods<L, R>().new Left(value);
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L getLeft();

    public abstract R getRight();

    public <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper, Consumer<Object> logging) {
        Objects.requireNonNull(leftMapper, "leftMapper is required");
        Objects.requireNonNull(rightMapper, "rightMapper is required");
        final Optional<Consumer<Object>> loggingOptional = Optional.ofNullable(logging);
        if (isLeft()) {
            loggingOptional.ifPresent(consumer -> consumer.accept(getLeft()));
            return leftMapper.apply(getLeft());
        } else {
            loggingOptional.ifPresent(consumer -> consumer.accept(getRight()));
            return rightMapper.apply(getRight());
        }
    }

    public <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper) {
        return fold(leftMapper, rightMapper, null);
    }

    public static class BaseMethods<L, R> extends Either<L, R> {

        @Override
        public boolean isLeft() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRight() {
            throw new UnsupportedOperationException();
        }

        @Override
        public L getLeft() {
            throw new UnsupportedOperationException();
        }

        @Override
        public R getRight() {
            throw new UnsupportedOperationException();
        }
    }

    class Left extends Either<L, R> {

        private L value;

        public Left(L value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L getLeft() {
            return this.value;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("There is no right in Left");
        }
    }

    class Right extends Either<L, R> {

        private R value;

        public Right(R value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("There is no left in Right");
        }

        @Override
        public R getRight() {
            return this.value;
        }
    }
}