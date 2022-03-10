package com.project.market.modules.product.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;

    private float rating;

    private int stock;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account")
    private Account seller;

    private LocalDateTime enrolledDateTime;

    @ManyToOne(fetch = LAZY)
    private Account purchasedBy;

    private LocalDateTime purchasedDateTime;

    @OneToOne(fetch = LAZY)
    private Delivery delivery;

    private String coverPhoto;

    private String photo;

    private boolean reserved;

    private boolean expired;

    @ManyToMany
    private List<Tag> tags;

}
