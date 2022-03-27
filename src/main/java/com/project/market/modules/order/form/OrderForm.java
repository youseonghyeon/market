package com.project.market.modules.order.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
public class OrderForm {

    @Positive
    private Long itemId;

    private String shippingRequests;

    private String recipient;
    private String recipientPhone;

    private String destinationZoneCode;
    @NotEmpty
    private String destinationAddress;

    private String destinationAddressDetail;

    private String paymentMethod;

    private String deliveryMethod;


    public OrderForm(Long itemId, String deliveryMethod) {
        this.itemId = itemId;
        this.deliveryMethod = deliveryMethod;
    }
}
