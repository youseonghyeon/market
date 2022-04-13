package com.project.market.modules.item.dao.repository;

import com.project.market.modules.item.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByTitle(String title);

    Set<Tag> findAllByTitleIn(Set<String> titles);

    // white list에 사용될 메서드
    List<Tag> findTop100ByOrderByCountDesc();
    List<Tag> findTop20ByOrderByCountDesc();

    boolean existsByTitle(String tag);
}
