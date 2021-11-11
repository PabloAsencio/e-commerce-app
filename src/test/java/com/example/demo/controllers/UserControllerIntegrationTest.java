package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    public static final String BASE_URL = "http://localhost:";
    public static final String API_ENDPOINT = "/api/user/";

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
    public void findByIdHappyPath() {
        Long userId = 1L;
        final ResponseEntity<User> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + "id/" + userId,
                User.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        final User user = response.getBody();
        assertEquals(userId, Long.valueOf(user.getId()));
        assertEquals("default", user.getUsername());
    }

    @Test
    public void findByIdNotFound() {
        Long userId = 2L;
        final ResponseEntity<User> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + "id/" + userId,
                User.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findByUserNameHappyPath() {
        String username = "default";
        final ResponseEntity<User> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + username,
                User.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertEquals(username, user.getUsername());
    }

    @Test
    public void findByUserNameNotFound() {
        String username = "username";
        final ResponseEntity<User> response = restTemplate.getForEntity(
                BASE_URL + port + API_ENDPOINT + username,
                User.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createUserHappyPath() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        final ResponseEntity<User> response = restTemplate.postForEntity(
                BASE_URL + port + API_ENDPOINT + "/create",
                createUserRequest,
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertNotNull(user.getId());
    }

    @Test
    public void createUserBadRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("wrongPassword");

        final ResponseEntity<User> response = restTemplate.postForEntity(
                BASE_URL + port + API_ENDPOINT + "/create",
                createUserRequest,
                User.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}