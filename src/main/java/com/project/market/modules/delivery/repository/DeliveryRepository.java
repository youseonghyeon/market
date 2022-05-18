package com.project.market.modules.delivery.repository;

import com.project.market.modules.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, CustomDeliveryRepository {

}
