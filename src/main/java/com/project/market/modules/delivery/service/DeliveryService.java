package com.project.market.modules.delivery.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.repository.DeliveryRepository;
import com.project.market.modules.delivery.entity.Delivery;
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
        Delivery delivery = Delivery.createNewDelivery(account, orderForm, item);
        return deliveryRepository.save(delivery);
    }

    public void departure(Delivery delivery) {
        // 예상 날짜
        LocalDate expectedArrivalFrom = LocalDate.now().plus(2, ChronoUnit.DAYS);
        LocalDate expectedArrivalUntil = LocalDate.now().plus(4, ChronoUnit.DAYS);
        delivery.startDelivery(expectedArrivalFrom, expectedArrivalUntil);
    }

    public void arrival(Delivery delivery) {
        delivery.completeDelivery();
    }

    public void cancel(Delivery delivery) {
        delivery.cancel();
    }
}
