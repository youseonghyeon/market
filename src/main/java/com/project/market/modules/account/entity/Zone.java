package com.project.market.modules.account.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Zone {

    @Id @GeneratedValue
    private Long id;

    private String city;

    private String gu;

    private String dong;

}
