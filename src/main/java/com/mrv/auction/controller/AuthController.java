package com.mrv.auction.controller;


import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;
import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.AuthService;
import com.mrv.auction.service.UserService;
import com.mrv.auction.validation.OnCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public JwtResponse login(@Validated @RequestBody final JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> register(@Validated(OnCreate.class)
                                            @ModelAttribute UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return new ResponseEntity<>(userMapper.toDto(userService.create(user)), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody final String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
