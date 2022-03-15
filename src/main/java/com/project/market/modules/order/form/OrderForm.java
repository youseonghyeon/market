package com.project.market.modules.order.form;

import com.project.market.modules.delivery.entity.DeliveryMethod;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
public class OrderForm {

    @Positive
    private Long itemId;

    private String shippingRequests;

    //TODO 가능하면 String 에서 Enum 으로 교체
    @NotEmpty
    private String paymentMethod;

    @NotEmpty
    private String destinationAddress;

    private DeliveryMethod deliveryMethod;


}
