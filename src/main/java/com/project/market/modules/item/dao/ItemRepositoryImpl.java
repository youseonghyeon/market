package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.project.market.modules.item.entity.QItem.item;
import static com.project.market.modules.item.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements CustomItemRepository {

    private final JPAQueryFactory queryFactory;

    public List<Item> findItemList(String tagName, String orderCriteria) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(tagName)) {
            builder.and(tag.title.eq(tagName));
        }
        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(builder)
                .where(item.expired.isFalse())
                .where(item.deleted.isFalse())
                .orderBy(itemSort(orderCriteria))
                .fetch();
    }

    private OrderSpecifier<?> itemSort(String criteria) {
        if ("popular".equals(criteria)) {
            return item.rating.desc();
        }
        return item.enrolledDateTime.desc();
    }

}
