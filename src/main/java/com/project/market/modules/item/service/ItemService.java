package com.project.market.modules.item.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.repository.FavoriteRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final NotificationRepository notificationRepository;
    private final FavoriteRepository favoriteRepository;

    public Item createNewItem(ItemForm itemForm) {
        Item item = Item.createNewItem(itemForm);
        editTags(item, itemForm);
        itemRepository.save(item);

//        item.uploadPhotos(itemForm);
        return item;
    }

    private void editTags(Item item, ItemForm itemForm) {
        Set<Tag> tagsInItem = item.getTags();
        tagsInItem.clear();

        Set<String> newTags = itemForm.getTags();
        if (!newTags.isEmpty()) {
            Set<Tag> findTags = tagRepository.findAllByTitleIn(newTags);
            tagsInItem.addAll(findTags);
        }
    }

    public void modifyItem(Item item, ItemForm itemForm) {
        item.editItem(itemForm);
        editTags(item, itemForm);

        itemRepository.save(item);
    }

    public void deleteItem(Item item) {
        notificationRepository.deleteByItemId(item.getId());
        item.delete();
    }

    public void addFavorite(Account account, Item item) {
        Favorite favorite = new Favorite(account, item);
        favoriteRepository.save(favorite);

        item.plusFavoriteCount();
    }

    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);

        Item item = favorite.getItem();
        item.minusFavoriteCount();
        itemRepository.save(item);
    }

    public void savePhotoPath(Item item, String coverPhotoPath, String photoPath) {
        item.setPhotoPaths(coverPhotoPath, photoPath);
    }
}
