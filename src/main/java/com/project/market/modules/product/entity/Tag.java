package com.project.market.modules.product.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tag {

    @Id @GeneratedValue
    private Long id;

    private String title;
}
