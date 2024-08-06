package com.mrv.auction.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;
import com.mrv.auction.model.User;
import com.mrv.auction.security.JwtTokenProvider;
import com.mrv.auction.service.AuthService;
import com.mrv.auction.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(JwtRequest loginRequest) {
        JwtResponse jwtResponse = new JwtResponse();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = userService.getByUsername(loginRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(
                user.getId(), user.getUsername(), user.getRole())
        );
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(
                user.getId(), user.getUsername())
        );
        return jwtResponse;
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        String token = "";

        try {
            JsonNode rootNode = objectMapper.readTree(refreshToken);
            token = rootNode.get("refreshToken").asText();

        }catch (Exception e){
            e.printStackTrace();
        }
        return jwtTokenProvider.refreshUserTokens(token);
    }
}
