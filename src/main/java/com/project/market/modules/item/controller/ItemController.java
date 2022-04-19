package com.project.market.modules.item.controller;

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


    @ExceptionHandler(IllegalAccessException.class)
    public String AccessEx() {
        log.warn("접근 권한 거부됨");
        return "exception/access";
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
    public String editMyProduct(@CurrentAccount Account account, @PathVariable("itemId") Item item, Model model) throws IllegalAccessException {
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        return "products/edit";
    }

    @PostMapping("/product/modify")
    public String modifyProduct(@CurrentAccount Account account, @ModelAttribute ItemForm itemForm, Errors errors) throws IllegalAccessException {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findById(itemForm.getId()).orElseThrow();
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        itemService.modifyItem(item, itemForm);
        return "redirect:/product/" + itemForm.getId();
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@CurrentAccount Account account, @RequestParam("itemId") Item item) throws IllegalAccessException {
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        if (item.isReserved()) {
            log.info("상품을 삭제할 수 없습니다.");
            // TODO 에러 메시지를 Json으로 보낼지 ErrorMsg를 보낼지 결정해야 함
        }
        if (item.deletable()) {
            itemService.deleteItem(item);
        }
        return "redirect:/product/my-list";
    }

    @PostMapping("/favorite/add")
    @ResponseBody
    public void addFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        if (favoriteRepository.existsByAccountAndItem(account, item)) {
            // 이미 등록됨
            return;
        }
        if (item.getEnrolledBy().equals(account)) {
            // 본인 상품
            return;
        }
        itemService.addFavorite(account, item);
    }

    @PostMapping("/favorite/delete")
    @ResponseBody
    public void deleteFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {

        Favorite favorite = favoriteRepository.findByAccountAndItem(account, item);
        if (favorite != null) {
            itemService.deleteFavorite(favorite);
        }
    }
}
