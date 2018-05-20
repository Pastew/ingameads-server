package com.pastew.ingameadsui.Payment;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Random;

@Entity
@Data
public class Payment {

    @Id
    private long id;
    private long offerId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public Payment() {
    }

    public Payment(long offerId) {
        id = new Random().nextLong();
        this.offerId = offerId;
        paymentStatus = PaymentStatus.PENDING;
    }
}
