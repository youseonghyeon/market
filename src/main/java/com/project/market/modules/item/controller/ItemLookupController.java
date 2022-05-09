package com.project.market.modules.item.controller;

import com.project.market.infra.exception.CustomNotFoundException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.repository.FavoriteRepository;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.dao.repository.TagRepository;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemLookupController {


    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final ItemService itemService;
    private final FavoriteRepository favoriteRepository;

    @GetMapping("/product/{itemId}")
    public String productForm(@CurrentAccount Account account, @PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findItemWithTagsById(itemId);
        if (item.isDeleted()) {
            throw new CustomNotFoundException("삭제된 상품입니다.");
        }

        if (account != null && favoriteRepository.existsByAccountAndItem(account, item)) {
            model.addAttribute("favorite", true);
        } else {
            model.addAttribute("favorite", false);
        }

        model.addAttribute("item", item);
        return "products/product";
    }

    @GetMapping("/product/list")
    public String productListForm(Pageable pageable,
                                  @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "tag", required = false) String tag,
                                  @RequestParam(value = "order", required = false) String orderBy, Model model) {

        Page<Item> itemPage = itemRepository.findItemList(search, tag, orderBy, pageable);
        List<Tag> tagList = tagRepository.findTop20ByOrderByCountDesc();

        model.addAttribute("itemPage", itemPage);
        model.addAttribute("itemList", itemPage.getContent());
        model.addAttribute("tagList", tagList);
        return "products/list";
    }

    @GetMapping("/product/my-list")
    public String myProductList(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByDeletedFalseOrderByEnrolledDateDesc();
        model.addAttribute("itemList", itemList);
        return "products/my-list";
    }

    @GetMapping("/favorite/list")
    public String favoriteListForm(@CurrentAccount Account account, Model model) {
        List<Item> favoriteItems = itemRepository.findFavoriteItems(account);
        model.addAttribute("favoriteList", favoriteItems);
        model.addAttribute("account", account);
        return "products/favorite-list";
    }

    @GetMapping("/cart")
    public String myCart(@CurrentAccount Account account, Model model) {

        return "products/cart";

    }
}
