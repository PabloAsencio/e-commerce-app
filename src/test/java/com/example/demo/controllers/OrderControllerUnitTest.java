package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class OrderControllerUnitTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    private OrderController orderController;

    @Before
    public void setUp() throws Exception {
        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setName("itemOne");
        itemOne.setDescription("itemOne");
        itemOne.setPrice(BigDecimal.valueOf(1.99));

        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setName("itemTwo");
        itemTwo.setDescription("itemTwo");
        itemTwo.setPrice(BigDecimal.valueOf(0.99));

        User user = new User();
        user.setId(1L);
        user.setUsername("username");

        Cart cartOne = new Cart();
        cartOne.setId(1L);
        cartOne.setUser(user);
        cartOne.addItem(itemOne);
        cartOne.addItem(itemOne);
        cartOne.addItem(itemTwo);
        user.setCart(cartOne);

        Cart cartTwo = new Cart();
        cartTwo.setId(2L);
        cartTwo.setUser(user);
        cartTwo.addItem(itemOne);
        cartTwo.addItem(itemTwo);
        cartTwo.addItem(itemTwo);

        UserOrder orderOne = UserOrder.createFromCart(cartOne);
        UserOrder orderTwo = UserOrder.createFromCart(cartTwo);
        List<UserOrder> allOrders = Arrays.asList(new UserOrder[]{orderOne, orderTwo});

        given(userRepository.findByUsername("username")).willReturn(user);
        given(userRepository.findByUsername("userNull")).willReturn(null);
        given(orderRepository.findByUser(any())).willReturn(allOrders);

        orderController = new OrderController(userRepository, orderRepository);
    }


    @Test
    public void submitHappyPath() {
        String username = "username";
        final ResponseEntity<UserOrder> response = orderController.submit(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder order = response.getBody();
        assertEquals(username, order.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(4.97), order.getTotal());
        assertEquals(3, order.getItems().size());

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    public void submitBadUser() {
        String username = "userNull";
        final ResponseEntity<UserOrder> response = orderController.submit(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getOrdersForUserHappyPath() {
        String username = "username";
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> orders = response.getBody();
        assertEquals(2, orders.size());
        assertEquals(username, orders.get(0).getUser().getUsername());
        assertEquals(username, orders.get(1).getUser().getUsername());
        assertEquals(BigDecimal.valueOf(4.97), orders.get(0).getTotal());
        assertEquals(BigDecimal.valueOf(3.97), orders.get(1).getTotal());
    }

    @Test
    public void getOrdersForUserBadUser() {
        String username = "userNull";
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}