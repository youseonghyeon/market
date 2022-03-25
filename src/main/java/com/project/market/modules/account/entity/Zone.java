package com.project.market.modules.account.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Zone {

    @Id @GeneratedValue
    private Long id;

    private String city;


}
