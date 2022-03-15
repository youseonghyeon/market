package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Orders;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    public Orders processPurchase(Account account, OrderForm orderForm) {
        //TODO 결제되었는지 확인
        Item item = itemRepository.findById(orderForm.getItemId()).orElseThrow();
        Delivery delivery = deliveryService.createDelivery(account, orderForm, item);
        return createOrders(account, orderForm, delivery, item);
    }

    @Transactional(readOnly = true)
    public List<Orders> findOrders(Account account) {
        return findOrders(account, null);
    }
    @Transactional(readOnly = true)
    public List<Orders> findOrders(Account account, String orderType) {
        if (orderType != null && orderType.equals("DELIVERY")) {
            return orderRepository.findByCustomerAndOrderStatusIs(account, OrderStatus.DELIVERY);
        } else {
            return orderRepository.findByCustomer(account);
        }
    }

    private Orders createOrders(Account account, OrderForm orderForm, Delivery delivery, Item item) {
        Orders orders = Orders.builder()
                .orderDateTime(LocalDateTime.now())
                .orderStatus(OrderStatus.PAYMENT)
                .paymentMethod(orderForm.getPaymentMethod())
                .shippingRequests(orderForm.getShippingRequests())
                .orderedItem(item)
                .orderDelivery(delivery)
                .customer(account)
                .shippingFee(item.getShippingFee())
                .build();
        return orderRepository.save(orders);
    }

}
