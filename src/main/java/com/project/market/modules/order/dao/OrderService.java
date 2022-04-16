package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.form.OrderForm;
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
        // 주문 취소
        if (paymentHasBeenMade(order)) {
            order.cancelOrderWithRefund();
        } else {
            order.cancelOrder();
        }

        // 배송 취소
        Delivery delivery = order.getOrderDelivery();
        if (delivery != null) {
            deliveryService.cancel(delivery);
        } else {
            log.info("Delivery가 존재하지 않습니다. orderId={}", order.getId());
        }

        // 상품 ROLLBACK
        Item item = order.getOrderedItem();
        if (item != null) {
            item.orderCancel();
        } else {
            log.info("Item이 존재하지 않습니다. orderId={}", order.getId());
        }
    }

    private boolean paymentHasBeenMade(Order order) {
        return order.getOrderStatus().equals(OrderStatus.PAYMENT);
    }
}
