package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class CartControllerUnitTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CartRepository cartRepository;

    private CartController cartController;

    @Before
    public void setUp() throws Exception {
        Cart cartOne = new Cart();
        User userOne = new User();
        userOne.setId(1L);
        userOne.setUsername("username");
        userOne.setCart(cartOne);
        cartOne.setId(1L);
        cartOne.setUser(userOne);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item);

        Cart cartTwo = new Cart();
        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setUsername("userTwo");
        userTwo.setCart(cartTwo);
        cartTwo.setId(2L);
        cartTwo.setUser(userTwo);
        cartTwo.setItems(items);
        cartTwo.setTotal(BigDecimal.valueOf(2.99 * 2));

        given(userRepository.findByUsername("username")).willReturn(userOne);
        given(userRepository.findByUsername("userTwo")).willReturn(userTwo);
        given(userRepository.findByUsername("userNull")).willReturn(null);
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));
        given(itemRepository.findById(2L)).willReturn(Optional.empty());
        cartController = new CartController(userRepository, cartRepository, itemRepository);
    }

    @Test
    public void addToCartOneItemHappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(Long.valueOf(1L), cart.getId());
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
        User user = cart.getUser();
        List<Item> items = cart.getItems();
        assertEquals(modifyCartRequest.getUsername(), user.getUsername());
        assertEquals(1, items.size());
        assertEquals(Long.valueOf(1L), items.get(0).getId());

        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void addToCartTwoItemsHappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(Long.valueOf(1L), cart.getId());
        assertEquals(BigDecimal.valueOf(2.99 * 2), cart.getTotal());
        User user = cart.getUser();
        List<Item> items = cart.getItems();
        assertEquals(modifyCartRequest.getUsername(), user.getUsername());
        assertEquals(2, items.size());
        assertEquals(Long.valueOf(1L), items.get(0).getId());
        assertEquals(Long.valueOf(1L), items.get(1).getId());

        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void addToCartBadUser() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userNull");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addToCartBadItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



    @Test
    public void removeFromCartAllHappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userTwo");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(Long.valueOf(2L), cart.getId());
        // You can't compare directly with equals BigDecimal(0) and BigDecimal(0.00)
        // See https://stackoverflow.com/questions/10950914/how-to-check-if-bigdecimal-variable-0-in-java/10950967
        assertTrue(cart.getTotal().compareTo(BigDecimal.ZERO) == 0);
        User user = cart.getUser();
        List<Item> items = cart.getItems();
        assertEquals(modifyCartRequest.getUsername(), user.getUsername());
        assertEquals(0, items.size());

        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void removeFromCartOneHappyPath() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userTwo");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(Long.valueOf(2L), cart.getId());
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
        User user = cart.getUser();
        List<Item> items = cart.getItems();
        assertEquals(modifyCartRequest.getUsername(), user.getUsername());
        assertEquals(1, items.size());
        assertEquals(Long.valueOf(1L), items.get(0).getId());

        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void removeFromCartBadUser() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userNull");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeFromCartBadItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userTwo");
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}