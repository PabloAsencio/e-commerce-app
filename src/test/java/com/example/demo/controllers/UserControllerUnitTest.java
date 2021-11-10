package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class UserControllerUnitTest {

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    private UserController userController;


    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        given(userRepository.findByUsername("username")).willReturn(user);
        given(userRepository.findByUsername("nullUser")).willReturn(null);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.findById(2L)).willReturn((Optional.empty()));

        userController = new UserController(userRepository, cartRepository, passwordEncoder);
    }

    @Test
    public void findByIdHappyPath() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Long actualId = response.getBody().getId();
        assertEquals(Long.valueOf(1L), actualId);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void findByIdNotFound() {
        final ResponseEntity<User> response = userController.findById(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    public void findByUserNameHappyPath() {
        String username = "username";
        final ResponseEntity<User> response = userController.findByUserName(username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(username, user.getUsername());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void findByUserNameNotFound() {
        String username = "userNull";
        final ResponseEntity<User> response = userController.findByUserName(username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void createUserHappyPath() {
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertNotNull(user.getId());

        verify(userRepository, times(1)).save(any());
        verify(cartRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    public void createUserHappyWithError() {
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("wrongPassword");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(userRepository, times(0)).save(any());
    }
}