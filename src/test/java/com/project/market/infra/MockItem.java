package com.project.market.infra;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.order.dao.OrderRepository;
import com.project.market.modules.order.dao.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class MockItem {


    @Autowired
    private ItemService itemService;

    public static int PRICE = 6000;
    public static int SHIPPING_PRICE = 2500;
    public static String DESC = "설명";
    public static int QUANTITY = 100;

    public Item createMockItem(String itemName) {
        ItemForm itemForm = createItemForm(itemName);
        return itemService.createNewItem(itemForm);
    }

    private ItemForm createItemForm(String itemName) {
        ItemForm itemForm = new ItemForm();
        itemForm.setName(itemName);
        itemForm.setPrice(PRICE);
        itemForm.setQuantity(QUANTITY);
        itemForm.setShippingFee(SHIPPING_PRICE);
        itemForm.setDescription(DESC);
        return itemForm;
    }
}
