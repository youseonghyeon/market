package com.project.market.modules.item.repository;

import com.project.market.modules.item.entity.option.OptionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OptionContentRepository extends JpaRepository<OptionContent, Long> {

    
}
