package com.project.market.modules.delivery.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;

public interface CustomDeliveryRepository {

    boolean isMyDelivery(Long accountId, Long deliveryId);
}
