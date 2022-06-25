package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.superclass.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    @Column(unique = true)
    private String title;

    @ColumnDefault("1")
    private long count;


    public Tag(String title, long count) {
        this.title = title;
        this.count = count;
    }
}
