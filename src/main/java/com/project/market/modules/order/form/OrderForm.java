package com.project.market.modules.order.form;

import com.project.market.modules.account.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class OrderForm {


    private String shippingRequests;

    private String recipient;
    private String recipientPhone;

    private String destinationZoneCode;
    private String destinationAddress;

    private String destinationAddressDetail;

    private String paymentMethod;


    public OrderForm(Account account) {
        destinationZoneCode = account.getZoneCode();
        destinationAddress = account.getRoadAddress();
        destinationAddressDetail = account.getAddressDetail();
    }
}
