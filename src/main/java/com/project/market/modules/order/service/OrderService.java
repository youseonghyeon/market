package com.project.market.modules.order.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.form.OrderForm;
import com.project.market.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryService deliveryService;

    public Order createOrder(Account account, OrderForm orderForm, Item item) {
        Order order = Order.createNewOrder(account, orderForm, item);
        orderRepository.save(order);
        item.sold();
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders(Account account, String orderType) {
        if (orderType == null) {
            return orderRepository.findByCustomerOrderByOrderDateDesc(account);
        }
        switch (orderType) {
            case "DELIVERY":
                return orderRepository.findByCustomerAndOrderStatusIsOrderByOrderDateDesc(account, OrderStatus.DELIVERY);
            case "OPTION2":
                return orderRepository.findByCustomerAndOrderStatusIsOrderByOrderDateDesc(account, OrderStatus.DELIVERY);
            case "OPTION3":
                return orderRepository.findByCustomerAndOrderStatusIsOrderByOrderDateDesc(account, OrderStatus.DELIVERY);
            default:
                return orderRepository.findByCustomerOrderByOrderDateDesc(account);
        }

    }

    public void mapping(Order order, Delivery delivery) {
        order.mappingDelivery(delivery);
    }

    public void paymentComplete(Order order) {
        order.paymentComplete();
    }

    public void cancelOrder(Order order) {
        // ?????? ??????
        if (paymentHasBeenMade(order)) {
            order.cancelOrderWithRefund();
        } else {
            order.cancelOrder();
        }

        // ?????? ??????
        Delivery delivery = order.getOrderDelivery();
        if (delivery != null) {
            deliveryService.cancel(delivery);
        } else {
            log.info("Delivery??? ???????????? ????????????. orderId={}", order.getId());
        }

        // ?????? ROLLBACK
        Item item = order.getOrderedItem();
        if (item != null) {
            item.orderCancel();
        } else {
            log.info("Item??? ???????????? ????????????. orderId={}", order.getId());
        }
    }

    private boolean paymentHasBeenMade(Order order) {
        return order.getOrderStatus().equals(OrderStatus.PAYMENT);
    }
}
