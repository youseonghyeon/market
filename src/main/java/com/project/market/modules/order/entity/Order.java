package com.project.market.modules.order.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(name = "withItemAndDelivery", attributeNodes = {
        @NamedAttributeNode("orderedItem"),
        @NamedAttributeNode("orderDelivery")})
@Entity(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

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

    public boolean isOwner(Account account) {
        // 객체 비교 시 Select account문이 실행되므로 ID값으로 비교함
        return this.customer.getId().equals(account.getId());
    }

    public void setOrderDelivery(Delivery orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public void payment() {
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
}
