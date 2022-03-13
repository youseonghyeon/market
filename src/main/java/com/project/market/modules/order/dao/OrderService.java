package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Orders;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void processPurchase(Account account, Item item, OrderForm orderForm) {
        Delivery delivery = createDelivery(orderForm, item);

        Orders orders = Orders.builder()
                .orderDateTime(LocalDateTime.now())
//                .orderStatus()  TODO 추가
                .paymentMethod(orderForm.getPaymentMethod())
                .orderedItem(item)
                .orderDelivery(delivery)
                .customer(account)
                .build();

        orderRepository.save(orders);
    }

    private Delivery createDelivery(OrderForm orderForm, Item item) {
        // TODO delivery 생성
        return null;
    }
}
