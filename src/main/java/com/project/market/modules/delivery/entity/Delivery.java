package com.project.market.modules.delivery.entity;

import com.project.market.modules.account.entity.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    private Long accountId;

    private int fee;

    private String recipient;
    private String recipientPhone;

    private String originZoneCode;
    private String originAddress;
    private String originAddressDetail;

    private String destinationZoneCode;
    private String destinationAddress;
    private String destinationAddressDetail;

    private LocalDate expectedArrivalFrom;

    private LocalDate expectedArrivalUntil;

    private String deliveryMethod;

    @Enumerated(STRING)
    private DeliveryStatus deliveryStatus;

    private String shippingCompany;

    private String shippingCode;

    private String trackingNumber;

    private String trackingUrl;


    public boolean isShipped() {
        return this.deliveryStatus.equals(DeliveryStatus.COMPLETE);
    }

    public boolean isOwner(Account account) {
        return this.accountId.equals(account.getId());
    }

    public void completeDelivery() {
        this.deliveryStatus = DeliveryStatus.COMPLETE;
    }

    public void shippingCancel() {
        if (this.deliveryStatus.equals(DeliveryStatus.COMPLETE)) {
            throw new IllegalStateException("취소 불가능");
        }
        this.deliveryStatus = DeliveryStatus.CANCEL;
    }

    public void startDelivery(LocalDate expectedArrivalFrom, LocalDate expectedArrivalUntil) {
        deliveryStatus = DeliveryStatus.PAYMENT;
        this.expectedArrivalFrom = expectedArrivalFrom;
        this.expectedArrivalUntil = expectedArrivalUntil;

    }
}
