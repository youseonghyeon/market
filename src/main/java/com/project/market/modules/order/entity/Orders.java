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

@NamedEntityGraph(name = "Orders.withItem", attributeNodes = {
        @NamedAttributeNode("orderedItem")})
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String paymentMethod;

    private String shippingRequests;

    private int shippingFee;

    private LocalDateTime arrivalDateTime;

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
        return this.customer.equals(account);
    }
}
