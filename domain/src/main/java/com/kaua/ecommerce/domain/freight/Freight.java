package com.kaua.ecommerce.domain.freight;

public class Freight {

    private final String cep;
    private final FreightType type;
    private final float price;
    private final int deadline;

    private Freight(final String aCep, final FreightType aType, final float aPrice, final int aDeadline) {
        this.cep = aCep;
        this.type = aType;
        this.price = aPrice;
        this.deadline = aDeadline;
    }

    public static Freight newFreight(
            final String aCep,
            final FreightType aType,
            final float aPrice,
            final int aDeadline
    ) {
        return new Freight(aCep, aType, aPrice, aDeadline);
    }

    public String getCep() {
        return cep;
    }

    public FreightType getType() {
        return type;
    }

    public float getPrice() {
        return price;
    }

    public int getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return "Freight(" +
                "cep='" + cep + '\'' +
                ", type=" + type.name() +
                ", price=" + price +
                ", deadline=" + deadline +
                ')';
    }
}
