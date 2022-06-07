package com.project.market.modules.item.entity.option;

import com.project.market.modules.item.entity.Item;
import com.project.market.modules.superclass.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class OptionTitle extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "option_title_id")
    private Long id;

    private String title;

    private Boolean required;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "item_id")
    private Item item;

    @OneToMany(mappedBy = "optionTitle", cascade = CascadeType.ALL)
    private List<OptionContent> optionContents = new ArrayList<>();


    public static OptionTitle createOptionTitle(String title, Item item, Boolean required) {
        OptionTitle optionTitle = new OptionTitle();
        optionTitle.title = title;
        optionTitle.item = item;
        optionTitle.required = required;
        return optionTitle;
    }
}
