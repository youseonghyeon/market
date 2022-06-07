package com.project.market.modules.item.controller;

import com.project.market.infra.fileupload.AwsS3Service;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.repository.FavoriteRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.service.OptionService;
import com.project.market.modules.item.service.TagService;
import com.project.market.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final TagService tagService;
    private final NotificationService notificationService;
    private final FavoriteRepository favoriteRepository;
    private final AwsS3Service awsS3Service;
    private final OptionService optionService;
    private final TagRepository tagRepository;

    @ExceptionHandler(IllegalStateException.class)
    public String ex(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "exception/item-ex";
    }


    @GetMapping("/product/enroll")
    public String productEnrollForm(Model model) {
        List<Tag> whiteList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("itemForm", new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/product/enroll")
    public String productEnroll(@Valid ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }

        tagService.createOrCountingTag(itemForm.getTags());
        Item item = itemService.createNewItem(itemForm);

        // 옵션 설정
        saveOption(item, itemForm.getOption1(), itemForm.getRequired1());
        saveOption(item, itemForm.getOption2(), itemForm.getRequired2());
        saveOption(item, itemForm.getOption3(), itemForm.getRequired3());

        // S3 이미지 저장 & 이미지 경로 저장
        savePhotoAndPath(item, itemForm);

        // 알람/메일 전송(비동기)
        notificationService.noticeItemEnrollment(item);
        notificationService.noticeByEmailItemEnrollment(item);
        return "redirect:/product/" + item.getId();
    }

    private void savePhotoAndPath(Item item, ItemForm itemForm) {
        // 값이 존재 할때만 저장, 아니면 유지
        String dir = "item/" + item.getId() + "/";
        String coverPhotoPath = item.getCoverPhotoPath();
        String photoPath = item.getPhotoPath();

        if (!itemForm.getCoverPhoto().isEmpty()) {
            coverPhotoPath = awsS3Service.uploadFile(dir, itemForm.getCoverPhoto());
        }
        if (!itemForm.getPhoto().isEmpty()) {
            photoPath = awsS3Service.uploadFile(dir, itemForm.getPhoto());
        }

        itemService.savePhotoPath(item, coverPhotoPath, photoPath);
    }

    private void saveOption(Item item, List<String> optionList, Boolean required) {
        String title = optionList.get(0);
        List<String> contentList = new ArrayList<>();
        if (!StringUtils.hasText(title)) {
            return;
        }
        for (int i = 1; i < optionList.size(); i++) {
            if (StringUtils.hasText(optionList.get(i))) {
                contentList.add(optionList.get(i));
            }
        }
        itemService.createItemOption(item, title, contentList, required);
    }


    @GetMapping("/product/modify/{itemId}")
    public String editMyProduct(@PathVariable("itemId") Item item, Model model) {
        // 어드민만 진입 가능
        // TODO 가격 수정 시 Cart 리포지토리에 있는 totalPrice의 대규모 연산(수정)이 필요함 (Sync 작업을 해주어야 함)
        List<Tag> whiteList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        model.addAttribute("tagList", item.getTags());
        return "products/edit";
    }

    @PutMapping("/product/modify/{itemId}")
    public String modifyProduct(@PathVariable("itemId") Long itemId, @ModelAttribute ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findItemWithTagsById(itemId);
        // 어드민만 진입 가능
        savePhotoAndPath(item, itemForm);

        tagService.createOrFindTags(itemForm.getTags());
        itemService.modifyItem(item, itemForm);
        return "redirect:/product/" + item.getId();
    }

    @DeleteMapping("/product/delete")
    public String deleteProduct(@RequestParam("itemId") Item item) {
        // 어드민만 진입 가능

        itemService.deleteItem(item);
        return "redirect:/product/modify";
    }

    @PostMapping("/favorite/toggle")
    @ResponseBody
    public String toggleFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        if (account == null) {
            return "fail";
        }
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
