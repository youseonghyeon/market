package com.project.market.modules.item.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class CartItem {

    @Id @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

}
