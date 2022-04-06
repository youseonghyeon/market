package com.project.market.modules.order.form;

import com.project.market.modules.account.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
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

    public OrderForm(Long itemId, String deliveryMethod, Account account) {
        this.itemId = itemId;
        this.deliveryMethod = deliveryMethod;
        destinationZoneCode = account.getZoneCode();
        destinationAddress = account.getRoadAddress();
        destinationAddressDetail = account.getAddressDetail();
    }
}
