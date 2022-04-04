package com.project.market.modules.delivery.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.entity.DeliveryStatus;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(Account account, OrderForm orderForm, Item item) {
        Delivery delivery = newDeliveryBuild(account, orderForm, item);
        return deliveryRepository.save(delivery);
    }

    private Delivery newDeliveryBuild(Account account, OrderForm orderForm, Item item) {
        return Delivery.builder()
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
//                .expectedArrivalFrom(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .expectedArrivalFrom(null)
//                .expectedArrivalUntil(LocalDate.now().plus(4, ChronoUnit.DAYS))
                .expectedArrivalUntil(null)
                .deliveryMethod(orderForm.getDeliveryMethod())
                .deliveryStatus(DeliveryStatus.WAITING)
                .shippingCompany("company")
                .shippingCode("test-shipping-code")
                .trackingNumber("test-tracking-number")
                .trackingUrl("test-tracking-url")
                .build();
    }

    public void competeDelivery(Delivery delivery) {
        delivery.completeDelivery();
    }

    public void startDelivery(Delivery delivery) {
        LocalDate expectedArrivalFrom = LocalDate.now().plus(2, ChronoUnit.DAYS);
        LocalDate expectedArrivalUntil = LocalDate.now().plus(4, ChronoUnit.DAYS);
        delivery.startDelivery(expectedArrivalFrom, expectedArrivalUntil);
    }
}
