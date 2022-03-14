package com.project.market.modules.order.form;

import com.project.market.modules.delivery.entity.DeliveryMethod;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class OrderForm {

    @Min(2)
    private Long itemId;

    private String shippingRequests;

    //TODO 가능하면 String 에서 Enum 으로 교체
    private String paymentMethod;

    private String destinationAddress;

    private DeliveryMethod deliveryMethod;


}
