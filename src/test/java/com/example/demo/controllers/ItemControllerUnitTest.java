package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class ItemControllerUnitTest {

    @Autowired
    private JacksonTester<Item> json;

    @MockBean
    private ItemRepository itemRepository;

    private ItemController itemController;

    private List<Item> items;

    @Before
    public void setUp() throws Exception {
        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setName("itemOne");
        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setName("itemTwo");
        items = Arrays.asList(new Item[]{itemOne, itemTwo});
        given(itemRepository.findAll()).willReturn(items);
        given(itemRepository.findByName("itemOne")).willReturn(Arrays.asList(new Item[]{itemOne}));
        given(itemRepository.findByName("itemTwo")).willReturn(Arrays.asList(new Item[]{itemTwo}));
        given(itemRepository.findByName("itemThree")).willReturn(Collections.emptyList());
        given(itemRepository.findByName("itemFour")).willReturn(null);
        given(itemRepository.findById(1L)).willReturn(Optional.of(itemOne));
        given(itemRepository.findById(2L)).willReturn(Optional.of(itemTwo));
        given(itemRepository.findById(3L)).willReturn(Optional.empty());
        itemController = new ItemController(itemRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getItems() throws Exception {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        final List<Item> actualItems = response.getBody();
        assertEquals( response.getStatusCode(), HttpStatus.OK);
        assertEquals(2, actualItems.size());
        assertEquals(this.items.get(0).getId(), actualItems.get(0).getId());
        assertEquals(this.items.get(1).getId(), actualItems.get(1).getId());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void getItemByIdHappyPath() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        Item item = response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(this.items.get(0).getId(), item.getId());
        assertEquals(this.items.get(0).getName(), item.getName());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void getItemByIdNotFound() {
        final ResponseEntity<Item> response = itemController.getItemById(3L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        verify(itemRepository, times(1)).findById(3L);
    }


    @Test
    public void getItemsByNameHappyPath() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("itemOne");
        final List<Item> items = response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(1, items.size());
        assertEquals(this.items.get(0).getId(), items.get(0).getId());
        assertEquals(this.items.get(0).getName(), items.get(0).getName());

        verify(itemRepository, times(1)).findByName("itemOne");
    }

    @Test
    public void getItemsByNameNotFoundEmptyList() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("itemThree");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        verify(itemRepository, times(1)).findByName("itemThree");
    }

    @Test
    public void getItemsByNameNotFoundNull() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("itemFour");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        verify(itemRepository, times(1)).findByName("itemFour");
    }
}