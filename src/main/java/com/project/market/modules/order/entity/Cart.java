package com.project.market.modules.order.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int price;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    public Cart(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice() * quantity;
    }

    public Cart(Item item, int quantity, Account account) {
        this.item = item;
        this.quantity = quantity;
        this.account = account;
        this.price = item.getPrice() * quantity;
    }

    public Cart(Item item, int quantity, Order order) {
        this.item = item;
        this.quantity = quantity;
        this.order = order;
        this.price = item.getPrice() * quantity;
    }

    public void handOverToOrder(Order order) {
        this.account = null;
        this.order = order;
    }

    public void modifyQuantity(int quantity) {
        this.quantity = quantity;
    }
}
