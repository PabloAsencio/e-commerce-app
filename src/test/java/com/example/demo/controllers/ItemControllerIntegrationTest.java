package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    public static final String BASE_URL = "http://localhost:";
    public static final String API_ENDPOINT = "/api/item/";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        TestUtils.authenticate(restTemplate, port);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getItems() {
        final ResponseEntity<Item[]> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT,
                Item[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void getItemByIdHappyPath() {
        Long itemId = 1L;
        final ResponseEntity<Item> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + itemId,
                Item.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        final Item item = response.getBody();
        assertEquals(itemId, item.getId());
        assertEquals("Round Widget", item.getName());
        assertEquals(BigDecimal.valueOf(2.99), item.getPrice());
        assertEquals("A widget that is round", item.getDescription());
    }

    @Test
    public void getItemsByNameHappyPath() {
        String itemName = "Square Widget";
        final ResponseEntity<Item[]> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + "/name/" + itemName,
                Item[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        final Item item = response.getBody()[0];
        assertEquals(Long.valueOf(2), item.getId());
        assertEquals(itemName, item.getName());
    }
}