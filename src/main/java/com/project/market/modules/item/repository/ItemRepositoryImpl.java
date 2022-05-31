package com.project.market.modules.item.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.QComment;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.project.market.modules.item.entity.QComment.comment;
import static com.project.market.modules.item.entity.QFavorite.favorite;
import static com.project.market.modules.item.entity.QItem.item;
import static com.project.market.modules.item.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRepositoryImpl implements CustomItemRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Item> findItemList(String search, String tagName, String order, Pageable pageable) {
        List<Item> content = getItems(search, order, pageable, tagName);
        int total = getItemsTotal(search, tagName);

        return new PageImpl<>(content, pageable, total);
    }

    private List<Item> getItems(String search, String order, Pageable pageable, String tagName) {
        return queryFactory.selectFrom(item).distinct()
                .leftJoin(item.tags, tag).fetchJoin()
                .leftJoin(item.commentList, comment).fetchJoin()
                .where(
                        item.deleted.isFalse(),
                        tagNameEq(tagName),
                        searchEq(search)
                )
                .orderBy(itemSort(order))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Predicate searchEq(String search) {
        return search != null ? item.name.contains(search) : null;
    }

    private int getItemsTotal(String search, String tagName) {
        return queryFactory.selectFrom(item)
                .leftJoin(item.tags, tag)
                .where(
                        item.deleted.isFalse(),
                        tagNameEq(tagName),
                        searchEq(search)
                )
                .fetch().size();
    }

    private Predicate tagNameEq(String tagName) {
        return StringUtils.hasText(tagName) ? tag.title.eq(tagName) : null;
    }

    private OrderSpecifier<?> itemSort(String criteria) {
        if (criteria == null) {
            return item.createdDate.desc();
        }
        switch (criteria) {
            case "popular":
                return item.star.desc();
            case "recent":
                return item.createdDate.desc();
            // case 추가 예정
            default:
                return item.createdDate.desc();
        }
    }


    @Override
    public List<Item> findFavoriteItems(Account account) {
        return queryFactory.select(item)
                .from(favorite)
                .join(favorite.item, item)
                .where(favorite.account.id.eq(account.getId()))
                .fetch();
    }
}
