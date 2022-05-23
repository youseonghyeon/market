package com.project.market.infra;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class MockCart {

    @Autowired
    CartRepository cartRepository;

    public Cart createMockCart(Account account, Item item, int quantity) {
        return cartRepository.save(new Cart(item, quantity, account));
    }
}
