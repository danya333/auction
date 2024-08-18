package com.mrv.auction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;
import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.AuthService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void login() throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("email");
        jwtRequest.setPassword("1234");

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setId(1L);
        jwtResponse.setUsername("email");
        jwtResponse.setAccessToken("accessToken");
        jwtResponse.setRefreshToken("refreshToken");

        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("email"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void register() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setSurname("Surname");
        user.setUsername("username");
        user.setPassword("1234");
        user.setPasswordConfirmation("1234");

        UserDto userDto = new UserDto();
        userDto.setName("Name");
        userDto.setSurname("Surname");
        userDto.setUsername("username");
        userDto.setPassword("1234");
        userDto.setPasswordConfirmation("1234");

        UserDto userDtoResponse = new UserDto();
        userDtoResponse.setName("Name");
        userDtoResponse.setSurname("Surname");
        userDtoResponse.setUsername("username");

//        when(userMapper.toEntity(userDto)).thenReturn(user);
//        when(userMapper.toDto(any(User.class))).thenReturn(userDtoResponse);
//        when(userService.create(any(User.class))).thenReturn(user);

//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username").value("username"))
//                .andExpect(jsonPath("$.name").value("Name"))
//                .andExpect(jsonPath("$.surname").value("Surname"));
    }



    @Test
    void refresh() throws Exception {
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setId(1L);
        jwtResponse.setUsername("username");
        jwtResponse.setAccessToken("accessToken");
        jwtResponse.setRefreshToken("refreshToken");

        String refreshToken = "refreshToken";

        when(authService.refresh(any(String.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }
}