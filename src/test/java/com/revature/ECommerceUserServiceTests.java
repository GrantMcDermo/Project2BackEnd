package com.revature;

import com.revature.models.Product;
import com.revature.models.User;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.UserRepository;

import com.revature.services.ProductService;
import com.revature.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

public class ECommerceUserServiceTests {

    @Mock
    UserRepository userRepository;
    @Mock
    User mockUser;

    private UserService userService;

    @BeforeEach void setUp(){
        this.userService = new UserService(this.userRepository);
    }

    @Test void whenCredentialsExist_findByCredentialsReturnsUser(){
        Mockito.when(userRepository.findByEmailAndPassword("example@example.com","password")).thenReturn(Optional.ofNullable(mockUser));
        Optional<User> returnedUser = userService.findByCredentials("example@example.com","password");

        assertTrue(returnedUser.get().equals(mockUser));
    }

    @Test void saveUser(){
        userService.save(mockUser);
        verify(userRepository).save(mockUser);
    }

}