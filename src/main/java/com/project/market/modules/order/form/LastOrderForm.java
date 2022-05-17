package com.project.market.modules.order.form;

import com.project.market.modules.account.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LastOrderForm {

    private String items;

    private String shippingRequests;

    private String recipient;
    private String recipientPhone;

    private String destinationZoneCode;
    private String destinationAddress;

    private String destinationAddressDetail;

    private String paymentMethod;


    public LastOrderForm(Account account) {
        destinationZoneCode = account.getZoneCode();
        destinationAddress = account.getRoadAddress();
        destinationAddressDetail = account.getAddressDetail();
    }
}
