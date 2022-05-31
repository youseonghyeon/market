package com.project.market.modules.item.repository;

import com.project.market.modules.item.entity.option.OptionTitle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OptionTitleRepository extends JpaRepository<OptionTitle, Long> {

    @EntityGraph(attributePaths = {"optionContents"}, type = EntityGraph.EntityGraphType.FETCH)
    List<OptionTitle> findAllByItemId(Long itemId);


}
