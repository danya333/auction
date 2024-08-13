package com.mrv.auction.controller;


import com.mrv.auction.dto.auth.JwtRequest;
import com.mrv.auction.dto.auth.JwtResponse;
import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.AuthService;
import com.mrv.auction.service.UserService;
import com.mrv.auction.validation.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "auth-controller",
        description = "Методы данного контроллера позволяют выполнить вход и регистрацию пользователя в приложении, " +
                "а также получить и обновить JWT токены")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "Логин",
            description = "Данный метод позволяет выполнить идентификацию и аутентификацию пользователя в приложении")
    public JwtResponse login(@Validated @RequestBody final JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Регистрация",
            description = "Данный метод позволяет выполнить регистрацию нового пользователя в приложении")
    public ResponseEntity<UserDto> register(@Validated(OnCreate.class)
                                            @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return new ResponseEntity<>(userMapper.toDto(userService.create(user)), HttpStatus.CREATED);
    }


    @PostMapping("/refresh")
    @Operation(summary = "Обновить access токен",
            description = "Данный метод позволяет выполнить обновление access токена при помощи refresh токена")
    public JwtResponse refresh(@RequestBody final String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
