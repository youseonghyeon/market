package com.project.market.modules.order.service;

import com.project.market.infra.config.Config;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.form.LastOrderForm;
import com.project.market.modules.order.form.OrderForm;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryService deliveryService;
    private final CartRepository cartRepository;

    public Order createOrder(Account account, LastOrderForm orderForm, Set<Cart> cartSet) {
        Order order = Order.createNewOrder(account, orderForm);
        int tempPrice = 0;
        orderRepository.save(order);
        for (Cart cart : cartSet) {
            cart.handOverToOrder(order);
            tempPrice += cart.getPrice();

            // 상품 수량 삭감
            Item item = cart.getItem();
            item.minusQuantity(cart.getQuantity());
        }
        cartRepository.saveAll(cartSet);
        // totalPrice/ShippingFee 설정
        order.setBill(tempPrice + Config.SHIPPING_FEE, Config.SHIPPING_FEE);

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
//        Item item = order.getOrderedItem();
//        if (item != null) {
////            item.orderCancel();
//            //TODO
//        } else {
//            log.info("Item이 존재하지 않습니다. orderId={}", order.getId());
//        }
    }

    private boolean paymentHasBeenMade(Order order) {
        return order.getOrderStatus().equals(OrderStatus.PAYMENT);
    }
}
