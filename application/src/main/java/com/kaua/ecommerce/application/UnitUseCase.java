package com.kaua.ecommerce.application;

public abstract class UnitUseCase<IN> {

    public abstract void execute(IN input);
}
