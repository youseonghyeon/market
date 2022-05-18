package com.project.market.infra;

import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.delivery.repository.DeliveryRepository;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class MockOrder {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryService deliveryService;


//    public Order createMockOrder(Account account, Long itemId) {
//        Item item = itemRepository.findById(itemId).orElseThrow();
//        if (item.getEnrolledBy().equals(account)) {
//            System.out.println("################ERROR###############");
//            System.out.println("#    판매자와 구매자가 같습니다.   #");
//            System.out.println("################ERROR###############");
//            return null;
//        }
//        OrderForm orderForm = new OrderForm();
//        orderForm.setItemId(itemId);
//        orderForm.setShippingRequests("shippingRequests");
//        orderForm.setRecipient("받는사람");
//        orderForm.setRecipientPhone("010-0000-0000");
//        orderForm.setDestinationAddress("받는사람 주소(Address)");
//        orderForm.setDestinationZoneCode("12334");
//        orderForm.setDestinationAddressDetail("102동 505호");
//        orderForm.setPaymentMethod("카드");
//        orderForm.setDeliveryMethod(item.isPost() ? "post" : "direct");
//        return orderService.createOrder(account, orderForm, item);
//    }
}
