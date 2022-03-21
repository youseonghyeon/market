package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.project.market.modules.item.entity.QItem.item;
import static com.project.market.modules.item.entity.QTag.tag;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements CustomItemRepository {

    private final JPAQueryFactory queryFactory;

    public List<Item> findItemList(String tagName, String orderCriteria) {
        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (StringUtils.hasText(tagName)) {
            whereBuilder.and(tag.title.eq(tagName));
        }

        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(whereBuilder)
                // TODO orderCriteria를 사용해서 동적쿼리 생성
                .orderBy(item.enrolledDateTime.desc())
                .fetch();
    }

}
