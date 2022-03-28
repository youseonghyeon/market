package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.project.market.modules.item.entity.QItem.item;
import static com.project.market.modules.item.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements CustomItemRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Item> findItemList(String tagName, String orderCondition, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        if (StringUtils.hasText(tagName)) {
            condition.and(tag.title.eq(tagName));
        }

        List<Item> content = getItems(orderCondition, pageable, condition);
        int total = getItemsTotal(condition);

        return new PageImpl<>(content, pageable, total);
    }

    private List<Item> getItems(String orderCondition, Pageable pageable, BooleanBuilder condition) {
        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(condition)
                .where(item.expired.isFalse())
                .where(item.deleted.isFalse())
                .orderBy(itemSort(orderCondition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private int getItemsTotal(BooleanBuilder condition) {
        return queryFactory.selectFrom(item)
                .leftJoin(item)
                .where(condition)
                .where(item.expired.isFalse())
                .where(item.deleted.isFalse())
                .fetch().size();
    }

    private OrderSpecifier<?> itemSort(String criteria) {
        if ("popular".equals(criteria)) {
            return item.rating.desc();
        }
        return item.enrolledDateTime.desc();
    }

}
