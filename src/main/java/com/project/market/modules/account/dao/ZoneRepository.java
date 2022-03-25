package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    boolean existsByCity(String city);

    Zone findByCity(String city);
}
