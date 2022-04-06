package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.QTag;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
        List<Item> content = getItems(orderCondition, pageable, tagName);
        int total = getItemsTotal(tagName);

        return new PageImpl<>(content, pageable, total);
    }

    private List<Item> getItems(String orderCondition, Pageable pageable, String tagName) {
        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(
                        item.expired.isFalse(),
                        item.deleted.isFalse(),
                        tagNameEq(tagName)
                )
                .orderBy(itemSort(orderCondition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private int getItemsTotal(String tagName) {
        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(
                        item.expired.isFalse(),
                        item.deleted.isFalse(),
                        tagNameEq(tagName)
                )
                .fetch().size();
    }
    private Predicate tagNameEq(String tagName) {
        return StringUtils.hasText(tagName) ? tag.title.eq(tagName) : null;
    }

    private OrderSpecifier<?> itemSort(String criteria) {
        if (criteria == null) {
            return item.enrolledDate.desc();
        }
        switch (criteria) {
            case "popular":
                return item.rating.desc();
            case "recent":
                return item.enrolledDate.desc();
            // case 추가 예정
            default:
                return item.enrolledDate.desc();
        }
    }

}
