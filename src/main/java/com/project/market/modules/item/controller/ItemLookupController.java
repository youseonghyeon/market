package com.project.market.modules.item.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.TagRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemLookupController {


    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;

    @GetMapping("/product/{itemId}")
    public String productForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findItemWithTagsAndSellerById(itemId);
        if (!item.isAccessible()) {
            //404 에러
            throw new IllegalStateException("삭제된 상품입니다.");
        }
        model.addAttribute("item", item);
        return "products/product";
    }

    @GetMapping("/product/list")
    public String productListForm(Pageable pageable,
                                  @RequestParam(value = "tag", required = false) String tag,
                                  @RequestParam(value = "order", required = false) String orderBy, Model model) {

        Page<Item> itemPage = itemRepository.findItemList(tag, orderBy, pageable);
        List<Tag> tagList = tagRepository.findTop20ByOrderByCountDesc();

        model.addAttribute("itemPage", itemPage);
        model.addAttribute("itemList", itemPage.getContent());
        model.addAttribute("tagList", tagList);
        return "products/list";
    }

    @GetMapping("/product/my-list")
    public String myProductList(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByEnrolledByAndDeletedFalseOrderByEnrolledDateDesc(account);
        model.addAttribute("itemList", itemList);
        return "products/my-list";
    }
}
