package com.project.market.infra;

import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.delivery.repository.DeliveryRepository;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.order.form.LastOrderForm;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.service.OrderService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class TestUtils {

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


    public Account createMockAccount(String loginId) {
        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId(loginId);
        signupForm.setPassword("password");
        signupForm.setUsername("홍길동");
        signupForm.setEmail("email@email.com");
        signupForm.setPhone(createRandomPhoneNumber());
        accountService.saveNewAccount(signupForm);
        return accountRepository.findByLoginId(loginId);
    }

    public Item createMockItem(Account account, String itemName) {
        ItemForm itemForm = new ItemForm();
        itemForm.setName(itemName);
        itemForm.setPrice(10000);
        itemForm.getTags().add("태그1");
        itemForm.getTags().add("태그2");
        return itemService.createNewItem(itemForm);
    }

    public Order createMockOrder(Account account, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        LastOrderForm orderForm = new LastOrderForm();
        orderForm.setShippingRequests("shippingRequests");
        orderForm.setRecipient("받는사람");
        orderForm.setRecipientPhone("010-0000-0000");
        orderForm.setDestinationAddress("받는사람 주소(Address)");
        orderForm.setDestinationZoneCode("12334");
        orderForm.setDestinationAddressDetail("102동 505호");
        orderForm.setPaymentMethod("카드");
        return orderService.createOrder(account, orderForm, null);
    }

    public Delivery createMockDelivery(Account account, Item item) {
        OrderForm orderForm = new OrderForm();
        orderForm.setShippingRequests("shippingRequests");
        orderForm.setRecipient("받는사람");
        orderForm.setRecipientPhone("010-0000-0000");
        orderForm.setDestinationAddress("받는사람 주소(Address)");
        orderForm.setDestinationZoneCode("12334");
        orderForm.setDestinationAddressDetail("102동 505호");
        orderForm.setPaymentMethod("카드");
        return deliveryService.createDelivery(account, orderForm, item);
    }

    public String createRandomPhoneNumber() {
        double number = Math.random();
        String s = Double.toString(number);
        return "010" + s.substring(2, 10);
    }
}
