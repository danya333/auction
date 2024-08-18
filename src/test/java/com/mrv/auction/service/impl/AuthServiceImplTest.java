package com.mrv.auction.service.impl;

import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;
import com.mrv.auction.model.Role;
import com.mrv.auction.model.User;
import com.mrv.auction.security.JwtTokenProvider;
import com.mrv.auction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    User user;
    JwtRequest loginRequest;

    String accessToken = "accessToken";
    String refreshToken = "refreshToken";


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setSurname("Surname");
        user.setUsername("username");
        user.setPassword("password");
        user.setPasswordConfirmation("password");
        user.setRole(Role.USER);

        loginRequest = new JwtRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
    }

    @Test
    void login() {

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userService.getByUsername("username")).thenReturn(user);

        when(jwtTokenProvider.createAccessToken(1L, user.getUsername(), user.getRole()))
                .thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(1L, user.getUsername()))
                .thenReturn(refreshToken);

        JwtResponse jwtResponse = authService.login(loginRequest);

        assertNotNull(jwtResponse);
        assertEquals(1L, jwtResponse.getId());
        assertEquals("username", jwtResponse.getUsername());
        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(
                argThat(token ->
                        token.getPrincipal().equals("username") &&
                                token.getCredentials().equals("password")
                )
        );
        verify(userService, times(1)).getByUsername("username");
        verify(jwtTokenProvider, times(1)).createAccessToken(1L, user.getUsername(), user.getRole());
        verify(jwtTokenProvider, times(1)).createRefreshToken(1L, user.getUsername());
    }

    @Test
    void refresh() {

        String refreshTokenJson = "{\"refreshToken\":\"refreshTokenData\"}";
        String extractedToken = "refreshTokenData";

        JwtResponse expectedResponse = new JwtResponse();
        expectedResponse.setAccessToken("newAccessToken");
        expectedResponse.setRefreshToken("newRefreshToken");

        when(jwtTokenProvider.refreshUserTokens(extractedToken)).thenReturn(expectedResponse);

        JwtResponse actualResponse = authService.refresh(refreshTokenJson);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());

        verify(jwtTokenProvider, times(1)).refreshUserTokens(extractedToken);
    }
}