package com.kaua.ecommerce.infrastructure.order.persistence;

import com.kaua.ecommerce.domain.order.OrderPayment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "orders_payments")
@Entity
public class OrderPaymentJpaEntity {

    @Id
    private String id;

    @Column(name = "payment_method_id", nullable = false)
    private String paymentMethodId;

    @Column(name = "installments", nullable = false)
    private int installments;

    public OrderPaymentJpaEntity() {
    }

    private OrderPaymentJpaEntity(
            final String id,
            final String paymentMethodId,
            final int installments
    ) {
        this.id = id;
        this.paymentMethodId = paymentMethodId;
        this.installments = installments;
    }

    public static OrderPaymentJpaEntity toEntity(final OrderPayment aOrderPayment) {
        return new OrderPaymentJpaEntity(
                aOrderPayment.getId().getValue(),
                aOrderPayment.getPaymentMethodId(),
                aOrderPayment.getInstallments()
        );
    }

    public OrderPayment toDomain() {
        return OrderPayment.with(
                getId(),
                getPaymentMethodId(),
                getInstallments()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }
}
