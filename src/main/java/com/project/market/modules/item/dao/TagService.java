package com.project.market.modules.item.dao;

import com.project.market.modules.item.dao.repository.TagRepository;
import com.project.market.modules.item.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.market.modules.item.entity.QTag.tag;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    // select(1) + update(1 or 2)
    public void createOrCountingTag(Set<String> tagTitles) {
        Set<Tag> newTagList = new HashSet<>();
        Set<Tag> findTags = tagRepository.findAllByTitleIn(tagTitles);
        // DB에 이미 존재하는 태그 이름들
        List<String> titleList = findTags.stream().map(Tag::getTitle).collect(Collectors.toList());
        // 이미 존재하는 태그.count += 1
        queryFactory.update(tag)
                .set(tag.count, tag.count.add(1))
                .where(tag.title.in(titleList))
                .execute();
        // 존재하지 않는 태그 생성
        for (String tagTitle : tagTitles) {
            if (!titleList.contains(tagTitle)) {
                newTagList.add(new Tag(tagTitle, 1));
            }
        }
        tagRepository.saveAll(newTagList);
        em.flush();
        em.clear();
    }

    public void createOrCountingTag(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag != null) {
            tag.setCount(tag.getCount() + 1);
        } else {
            Tag newTag = new Tag(tagTitle, 1);
            tagRepository.save(newTag);
        }
        em.flush();
        em.clear();
    }


    public Tag createOrFindTag(String title) {
        Tag tag = tagRepository.findByTitle(title);
        if (tag != null) {
            return tag;
        } else {
            Tag newTag = new Tag(title, 1);
            return tagRepository.save(newTag);
        }
    }
}
