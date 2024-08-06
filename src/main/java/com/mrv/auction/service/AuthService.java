package com.mrv.auction.service;

import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;

public interface AuthService {


    JwtResponse login(
            JwtRequest loginRequest
    );

    JwtResponse refresh(
            String refreshToken
    );

}
