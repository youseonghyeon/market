package com.project.market.modules.delivery.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.entity.DeliveryMethod;
import com.project.market.modules.delivery.entity.DeliveryStatus;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static javax.persistence.EnumType.STRING;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(Account account, OrderForm orderForm, Item item) {
        Delivery delivery = Delivery.builder()
                .accountId(account.getId())
                .fee(item.getShippingFee())
                .recipient(orderForm.getRecipient())
                .recipientPhone(orderForm.getRecipientPhone())
                .originZoneCode(null)
                .originAddress(item.getOriginAddress())
                .originAddressDetail(null)
                .destinationZoneCode(orderForm.getDestinationZoneCode())
                .destinationAddress(orderForm.getDestinationAddress())
                .destinationAddressDetail(orderForm.getDestinationAddressDetail())
                .expectedArrivalFrom(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .expectedArrivalUntil(LocalDate.now().plus(4, ChronoUnit.DAYS))
                .deliveryMethod(DeliveryMethod.valueOf(orderForm.getDeliveryMethod()))
                .deliveryStatus(DeliveryStatus.READY)
                .shippingCompany("company")
                .shippingCode("test-shipping-code")
                .trackingNumber("test-tracking-number")
                .trackingUrl("test-tracking-url")
                .build();


        return deliveryRepository.save(delivery);
    }

    public void competeDelivery(Delivery delivery) {
        delivery.completeDelivery();
    }
}
