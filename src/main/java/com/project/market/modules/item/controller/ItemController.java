package com.project.market.modules.item.controller;

import com.project.market.infra.aop.Trace;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.dao.repository.FavoriteRepository;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.dao.repository.TagRepository;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.validator.ItemFormValidator;
import com.project.market.modules.item.validator.ItemValidator;
import com.project.market.modules.notification.dao.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ItemFormValidator itemFormValidator;
    private final NotificationService notificationService;
    private final FavoriteRepository favoriteRepository;
    private final ItemValidator itemValidator;


    @ExceptionHandler(IllegalStateException.class)
    public String ex(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "exception/item-ex";
    }

    @InitBinder("itemForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(itemFormValidator);
    }

    @GetMapping("/product/enroll")
    public String productEnrollForm(Model model) {
        List<Tag> whiteList = tagRepository.findTop100ByOrderByCountDesc(); // 태그 목록 (최대 상위 100개)
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("itemForm", new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/product/enroll")
    public String productEnroll(@CurrentAccount Account account, @Valid ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        tagService.createOrCountingTag(itemForm.getTags());
        Item item = itemService.createNewItem(account, itemForm);

        // 알람 전송(비동기)
        notificationService.noticeItemEnrollment(item);
        // 메일 전송(비동기)
        notificationService.noticeByEmailItemEnrollment(item);
        return "redirect:/product/" + item.getId();
    }

    @GetMapping("/product/edit/{itemId}")
    public String editMyProduct(@CurrentAccount Account account, @PathVariable("itemId") Item item, Model model) {
        itemValidator.modifyItemValidator(account, item);

        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        model.addAttribute("tagList", item.getTags());
        return "products/edit";
    }

    @PostMapping("/product/modify")
    public String modifyProduct(@CurrentAccount Account account, @ModelAttribute ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findItemWithTagsById(itemForm.getId());
        itemValidator.modifyItemValidator(account, item);

        tagService.createOrFindTags(itemForm.getTags());
        itemService.modifyItem(item, itemForm);
        return "redirect:/product/" + item.getId();
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        itemValidator.deleteItemValidator(account, item);

        itemService.deleteItem(item);
        return "redirect:/product/my-list";
    }


    @PostMapping("/favorite/toggle")
    @ResponseBody
    public String toggleFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        Favorite favorite = favoriteRepository.findByAccountAndItem(account, item);
        if (favorite != null) {
            itemService.deleteFavorite(favorite);
            return "delete";
        } else {
            itemService.addFavorite(account, item);
            return "add";
        }
    }
}
