package com.project.market.modules.item.dao;

import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    public void before() {
        Tag tag1 = new Tag("수박", 100);
        Tag tag2 = new Tag("참외", 90);
        Tag tag3 = new Tag("사과", 110);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
    }

    @Test
    void findTop100ByOrderByCountDesc() {
        List<Tag> tags = tagRepository.findTop100ByOrderByCountDesc();
        for (Tag tag : tags) {
            System.out.println("tag = " + tag.getTitle() + "(" + tag.getCount() + ")");
        }
    }
}
