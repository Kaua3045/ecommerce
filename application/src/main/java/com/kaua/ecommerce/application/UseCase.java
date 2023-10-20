package com.kaua.ecommerce.application;

public abstract class UseCase<OUT, IN> {

    public abstract OUT execute(IN input);
}
