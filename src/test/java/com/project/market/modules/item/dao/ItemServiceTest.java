package com.project.market.modules.item.dao;

import com.project.market.WithAccount;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;
    @Autowired
    MockItem mockItem;

    private String ITEM_NAME = "테스트 상품";
    private int PRICE = 41000;
    private int QUANTITY = 1200;

    @Test
    @DisplayName("상품 등록")
    @WithAccount("testAdmin")
    void createNewItem() {
        // given
        ItemForm itemForm = createItemForm();
        //when
        itemService.createNewItem(itemForm);
        //then
        Item item = itemRepository.findByName(ITEM_NAME);
        assertEquals(item.getPrice(), PRICE);
        assertEquals(item.getQuantity(), QUANTITY);
    }

    @Test
    @DisplayName("상품 수정")
    @WithAccount("testAdmin")
    void modifyItem() {
        //given
        Item item = mockItem.createMockItem(ITEM_NAME);
        //when
        ItemForm newForm = new ItemForm();
        newForm.setName("상품2");
        newForm.setPrice(2000);
        newForm.setQuantity(2000);
        newForm.setDescription("상품 설명");
        itemService.modifyItem(item, newForm);
        //then
        assertEquals(item.getName(), "상품2");
        assertEquals(item.getPrice(), 2000);
        assertEquals(item.getQuantity(), 2000);
        assertEquals(item.getDescription(), "상품 설명");
    }

    private ItemForm createItemForm() {
        ItemForm itemForm = new ItemForm();
        itemForm.setName(ITEM_NAME);
        itemForm.setPrice(PRICE);
        itemForm.setQuantity(QUANTITY);
        return itemForm;
    }
}
