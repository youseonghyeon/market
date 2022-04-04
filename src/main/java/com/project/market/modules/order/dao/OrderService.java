package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Order;
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

    public Order createOrder(Account account, OrderForm orderForm, Item item) {
        Order order = newOrderBuild(account, orderForm, item);
        orderRepository.save(order);
        item.sold();
        return order;
    }

    private Order newOrderBuild(Account account, OrderForm orderForm, Item item) {
        return Order.builder()
                .orderDateTime(LocalDateTime.now())
                .orderStatus(OrderStatus.WAITING)
                .paymentMethod(orderForm.getPaymentMethod())
                .shippingRequests(orderForm.getShippingRequests())
                .totalPrice(item.getPrice() + item.getShippingFee())
                .shippingFee(item.getShippingFee())
                .orderedItem(item)
                .customer(account)
                .build();
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders(Account account) {
        return findOrders(account, null);
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders(Account account, String orderType) {
        if (orderType != null && orderType.equals("DELIVERY")) {
            return orderRepository.findByCustomerAndOrderStatusIsOrderByOrderDateTimeDesc(account, OrderStatus.DELIVERY);
        } else {
            return orderRepository.findByCustomerOrderByOrderDateTimeDesc(account);
        }
    }

    public void join(Order order, Delivery delivery) {
        order.setOrderDelivery(delivery);
    }

    public void payment(Order order) {
        order.payment();
    }
}
