package com.project.market.modules.item.controller;

import com.project.market.infra.exception.CustomNotFoundException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Comment;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.entity.option.OptionTitle;
import com.project.market.modules.item.repository.*;
import com.project.market.modules.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemLookupController {


    private final ItemRepository itemRepository;
    private final FavoriteRepository favoriteRepository;
    private final TagRepository tagRepository;
    private final OptionTitleRepository optionTitleRepository;
    private final CommentRepository commentRepository;

    @Value("${shipping.fee}")
    private int shippingFee;

    @GetMapping("/product")
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

    @GetMapping("/product/{itemId}")
    public String productForm(@CurrentAccount Account account, @PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findItemWithTagsById(itemId);
        if (item.isDeleted()) {
            throw new CustomNotFoundException("삭제된 상품입니다.");
        }
        List<OptionTitle> optionTitleList = optionTitleRepository.findAllByItemId(itemId);
        List<Comment> comments = commentRepository.findCommentsByItemIdOrderByCreatedDateDesc(itemId);

        model.addAttribute("item", item);
        model.addAttribute("favorite", account != null && favoriteRepository.existsByAccountAndItem(account, item));
        model.addAttribute("optionTitleList", optionTitleList);
        model.addAttribute("shippingFee", shippingFee);

        model.addAttribute("commentList", comments);
        model.addAttribute("optionTitleIds", optionTitleList.stream().map(OptionTitle::getId).collect(Collectors.toList()));

        return "products/product";
    }


    @GetMapping("/product/modify")
    public String myProductListForm(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByDeletedFalseOrderByCreatedDateDesc();
        model.addAttribute("itemList", itemList);
        return "products/modify-list";
    }

    @GetMapping("/favorite")
    public String favoriteListForm(@CurrentAccount Account account, Model model) {
        List<Item> favoriteItems = itemRepository.findFavoriteItems(account);
        model.addAttribute("favoriteList", favoriteItems);
        model.addAttribute("account", account);
        return "products/favorite-list";
    }

}
