package com.project.market.modules.order.form;

import com.project.market.modules.delivery.entity.DeliveryMethod;
import lombok.Data;

@Data
public class OrderForm {

    private Long itemId;

    private String shippingRequests;

    //TODO 가능하면 String 에서 Enum 으로 교체
    private String paymentMethod;

    private String destinationAddress;

    private DeliveryMethod deliveryMethod;


}
