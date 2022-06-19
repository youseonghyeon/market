package com.project.market.modules.item.entity.option;

import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.superclass.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class OptionContent extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "option_content_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_title_id")
    private OptionTitle optionTitle;

    public OptionContent(OptionTitle optionTitle, String content) {
        this.content = content;
        this.optionTitle = optionTitle;
    }

    public static void createOptionContent(OptionTitle optionTitle, List<String> contentList) {
        for (String content : contentList) {
            OptionContent optionContent = new OptionContent(optionTitle, content);
            optionContent.getOptionTitle().getOptionContents().add(optionContent);
        }

    }

}
