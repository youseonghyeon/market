package com.project.market.modules.delivery.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.entity.DeliveryStatus;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(Account account, OrderForm orderForm, Item item) {
        Delivery delivery = Delivery.builder()
                .accountId(account.getId())
                .fee(item.getShippingFee())
                .originAddress(item.getOriginAddress())
                .destinationAddress(orderForm.getDestinationAddress())
                .expectedArrivalFrom(LocalDate.now().plus(2, ChronoUnit.DAYS))
                // TODO 예상 도착일자 공휴일 처리를 해주어야 함
                .expectedArrivalUntil(LocalDate.now().plus(4, ChronoUnit.DAYS))
                .deliveryMethod(orderForm.getDeliveryMethod())
                .deliveryStatus(DeliveryStatus.READY)
                .shippingCompany("test-company")
                .shippingCode("test-shipping-code")
                .trackingNumber("test-tracking-number")
                .trackingUrl("test-tracking-url")
                .build();
        return deliveryRepository.save(delivery);
    }

    public void competeDelivery(Delivery delivery) {
        delivery.completeDelivery();
    }
}
