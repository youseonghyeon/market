package com.project.market.modules.order.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.form.OrderForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "withItemAndDelivery", attributeNodes = {
        @NamedAttributeNode("orderedItem"),
        @NamedAttributeNode("orderDelivery")})
@Entity(name = "orders")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.WAITING;

    private String paymentMethod;

    private String shippingRequests;

    private int totalPrice;

    private int shippingFee;

    private LocalDateTime arrivalDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item orderedItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery orderDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account customer;

    @OneToMany(mappedBy = "order")
    private Set<CartItem> cartItems = new HashSet<>();



    public static Order createNewOrder(Account customer, OrderForm orderForm) {
        Order order = new Order();
        order.paymentMethod = orderForm.getPaymentMethod();
        order.shippingRequests = orderForm.getShippingRequests();
        // TODO !!!
//        order.totalPrice = item.getPrice() + item.getShippingFee();
//        order.orderedItem = item;
        order.customer = customer;
        return order;
    }

    public boolean isOwner(Account account) {
        // 객체 비교 시 Select account문이 실행되므로 ID값으로 비교함
        return this.customer.getId().equals(account.getId());
    }

    public void mappingDelivery(Delivery orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public void paymentComplete() {
        orderStatus = OrderStatus.PAYMENT;
    }

    public String getOrderStatusKo() {
        switch (orderStatus) {
            case WAITING:
                return "입금 대기중";
            case PAYMENT:
                return "결제 완료";
            case DELIVERY:
                return "배송중";
            case CANCEL:
                return "취소됨";
            default:
                return "";
        }
    }

    public void cancelOrder() {
        orderStatus = OrderStatus.CANCEL;
    }

    public void cancelOrderWithRefund() {
        orderStatus = OrderStatus.REFUND;
    }
}
