package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.entity.DeliveryMethod;
import com.project.market.modules.delivery.entity.DeliveryStatus;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Orders;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final DeliveryRepository deliveryRepository;

    public Orders processPurchase(Account account, OrderForm orderForm) {
        //TODO 결제되었는지 확인
        log.info("itemId={}", orderForm.getItemId());


        Item item = itemRepository.findById(orderForm.getItemId()).orElseThrow();

        // 배송 객체 생성
        Delivery delivery = createDelivery(account, orderForm, item);
        deliveryRepository.save(delivery);

        // 주문 객체 생성
        Orders orders = createOrders(account, orderForm, delivery, item);
        return orderRepository.save(orders);
    }

    public List<Orders> findOrders(Account account) {
        return findOrders(account, null);
    }

    @Transactional(readOnly = true)
    public List<Orders> findOrders(Account account, String orderType) {
        if (orderType != null && orderType.equals("DELIVERY")) {
            return orderRepository.findByCustomerAndOrderStatusIs(account, OrderStatus.DELIVERY);
        } else {
            return account.getOrders();
        }
    }

    private Orders createOrders(Account account, OrderForm orderForm, Delivery delivery, Item item) {
        return Orders.builder()
                .orderDateTime(LocalDateTime.now())
                .orderStatus(OrderStatus.PAYMENT)
                .paymentMethod(orderForm.getPaymentMethod())
                .shippingRequests(orderForm.getShippingRequests())
                .orderedItem(item)
                .orderDelivery(delivery)
                .customer(account)
                .shippingFee(item.getShippingFee())
                .build();
    }

    private Delivery createDelivery(Account account, OrderForm orderForm, Item item) {
        return Delivery.builder()
                .fee(item.getShippingFee())
                .originAddress(item.getOriginAddress())
                .destinationAddress(orderForm.getDestinationAddress())
                .expectedArrivalFrom(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .expectedArrivalUntil(LocalDate.now().plus(4, ChronoUnit.DAYS))
                .deliveryMethod(orderForm.getDeliveryMethod())
                .deliveryStatus(DeliveryStatus.READY)
                .shippingCompany("test-company")
                .shippingCode("test-shipping-code")
                .trackingNumber("test-tracking-number")
                .trackingUrl("test-tracking-url")
                .build();
    }
}
