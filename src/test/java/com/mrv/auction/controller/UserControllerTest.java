package com.mrv.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserTest() throws Exception {
        User user = new User();

        UserDto userDto = new UserDto();
        userDto.setName("NameTest");
        userDto.setSurname("SurnameTest");
        userDto.setUsername("username_test@gmail.com");

        when(userService.getById(anyLong())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NameTest"))
                .andExpect(jsonPath("$.surname").value("SurnameTest"))
                .andExpect(jsonPath("$.username").value("username_test@gmail.com"));
    }


    @Test
    void updateUserTest() throws Exception {
        User user = new User();
        UserDto userDto = new UserDto();
        User user2 = new User();

        UserDto userDto2 = new UserDto();
        userDto2.setName("NameTestChanged");
        userDto2.setSurname("SurnameTest");
        userDto2.setUsername("username_test@gmail.com");

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userService.update(anyLong(), any(User.class))).thenReturn(user2);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        mockMvc.perform(put("/api/users/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NameTestChanged"))
                .andExpect(jsonPath("$.surname").value("SurnameTest"))
                .andExpect(jsonPath("$.username").value("username_test@gmail.com"));
    }

    @Test
    void deleteUserTest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }
}
