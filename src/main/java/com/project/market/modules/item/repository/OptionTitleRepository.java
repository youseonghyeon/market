package com.project.market.modules.item.repository;

import com.project.market.modules.item.entity.option.OptionTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OptionTitleRepository extends JpaRepository<OptionTitle, Long> {


}
