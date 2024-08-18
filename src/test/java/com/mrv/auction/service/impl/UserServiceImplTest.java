package com.mrv.auction.service.impl;

import com.mrv.auction.model.Role;
import com.mrv.auction.model.User;
import com.mrv.auction.repository.UserRepository;
import com.mrv.auction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @Mock
    PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name1");
        user.setSurname("Surname");
        user.setUsername("username");
        user.setPassword("password");
        user.setPasswordConfirmation("password");
        user.setRole(Role.USER);
    }


    @Test
    void getCurrentUser() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("username");
        SecurityContextHolder.setContext(securityContext);

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals("username", result.getUsername());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    void getById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals("username", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getByUsername() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        User result = userService.getByUsername("username");
        assertNotNull(result);
        assertEquals("username", result.getUsername());
        verify(userRepository, times(1)).findByUsername("username");
    }

    @Test
    void update() {
        Long id = 1L;
        user.setUsername("updated username");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("updated encoded password");

        User result = userService.update(id, user);
        assertNotNull(result);
        assertEquals("updated username", result.getUsername());
        assertEquals("updated encoded password", result.getPassword());
    }

    @Test
    void create() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded password");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);
        assertNotNull(result);
        assertEquals("username", result.getUsername());
        assertEquals("encoded password", result.getPassword());
    }

    @Test
    void delete() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);
        userService.delete(id);
        assertTrue(userRepository.existsById(id));
        verify(userRepository, times(1)).deleteById(id);
    }
}