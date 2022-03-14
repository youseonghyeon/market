package com.project.market.modules.delivery.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    private int fee;

    private String originAddress;

    private String destinationAddress;

    private LocalDate expectedArrivalFrom;

    private LocalDate expectedArrivalUntil;

    @Enumerated(STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(STRING)
    private DeliveryStatus deliveryStatus;

    private String shippingCompany;

    private String shippingCode;

    private String trackingNumber;

    private String trackingUrl;


    public boolean isShipped() {
        return this.deliveryStatus.equals(DeliveryStatus.COMPLETE);
    }



}
