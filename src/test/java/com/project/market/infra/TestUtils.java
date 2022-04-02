package com.project.market.infra;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.order.dao.OrderRepository;
import com.project.market.modules.order.dao.OrderService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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
        itemForm.setCoverPhoto("test.jpg");
        itemForm.setPhoto("test.jpg");
        itemForm.setOriginAddress("서울시 은평구");
        itemForm.setPost(true);
        itemForm.setDirect(false);
        itemService.createNewItem(account, itemForm, new ArrayList<>());
        return itemRepository.findByName(itemName);
    }

    public Order createMockOrder(Account account, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (item.getEnrolledBy().equals(account)) {
            System.out.println("################ERROR###############");
            System.out.println("#    판매자와 구매자가 같습니다.   #");
            System.out.println("################ERROR###############");
            return null;
        }
        OrderForm orderForm = new OrderForm();
        orderForm.setItemId(itemId);
        orderForm.setShippingRequests("shippingRequests");
        orderForm.setRecipient("받는사람");
        orderForm.setRecipientPhone("010-0000-0000");
        orderForm.setDestinationAddress("받는사람 주소(Address)");
        orderForm.setDestinationZoneCode("12334");
        orderForm.setDestinationAddressDetail("102동 505호");
        orderForm.setPaymentMethod("카드");
        orderForm.setDeliveryMethod(item.isPost() ? "post" : "direct");
        return orderService.createOrder(account, orderForm, item);
    }

    public Delivery createMockDelivery(Account account, Item item) {
        if (item.getEnrolledBy().equals(account)) {
            System.out.println("################ERROR###############");
            System.out.println("#    판매자와 구매자가 같습니다.   #");
            System.out.println("################ERROR###############");
            return null;
        }
        OrderForm orderForm = new OrderForm();
        orderForm.setItemId(item.getId());
        orderForm.setShippingRequests("shippingRequests");
        orderForm.setRecipient("받는사람");
        orderForm.setRecipientPhone("010-0000-0000");
        orderForm.setDestinationAddress("받는사람 주소(Address)");
        orderForm.setDestinationZoneCode("12334");
        orderForm.setDestinationAddressDetail("102동 505호");
        orderForm.setPaymentMethod("카드");
        orderForm.setDeliveryMethod(item.isPost() ? "post" : "direct");
        return deliveryService.createDelivery(account, orderForm, item);
    }

    public String createRandomPhoneNumber() {
        double number = Math.random();
        String s = Double.toString(number);
        return "010" + s.substring(2, 10);
    }
}
