package com.project.market.modules.item.validator;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemValidator {

    public void modifyItemValidator(Account account, Item item) {
        if (item.isReserved() || item.isDeleted()) {
            throw new IllegalStateException("예약된 상품은 수정할 수 없습니다.");
        }
        if (!item.isMyItem(account)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }
    }

    public void deleteItemValidator(Account account, Item item) {
        if (item.isReserved() || item.isDeleted()) {
            throw new IllegalStateException("예약된 상품은 삭제할 수 없습니다.");
        }
        if (!item.isMyItem(account)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }
    }
}
