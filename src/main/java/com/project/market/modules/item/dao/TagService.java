package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public void createOrCountingTags(List<String> tags) {
        List<Tag> findTags = tagRepository.findAllByTitleIn(tags);
        List<String> collect = findTags.stream().map(Tag::getTitle).collect(Collectors.toList());

        List<Tag> newTags = new ArrayList<>();
        for (String title : tags) {
            if (!collect.contains(title)) {
                // Tag 테이블에 저장되있지 않는 태그는 새로 생성
                newTags.add(new Tag(title, 1));
            }
        }
        for (Tag findTag : findTags) {
            // Tag 테이블에 저장되어있는 태그는 count를 1 증가
            findTag.setCount(findTag.getCount() + 1);
        }
        if (!newTags.isEmpty()) {
            tagRepository.saveAll(newTags);
        }
    }

    public Tag findOrCreateTag(String title) {
        Tag tag = tagRepository.findByTitle(title);
        if (tag != null) {
            return tag;
        }
        Tag newTag = new Tag(title, 1);
        return tagRepository.save(newTag);
    }
}
