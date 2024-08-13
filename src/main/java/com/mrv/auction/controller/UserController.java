package com.mrv.auction.controller;

import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
@Tag(name = "user-controller",
        description = "Методы данного контроллера позволяют выполнять CRUD операции с пользователем")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id",
            description = "Данный метод позволяет получить информацию о пользователе по id")
    public ResponseEntity<UserDto> getUser(@Parameter(description = "id пользователя")
                                           @PathVariable Long id) {
        User user = userService.getById(id);
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя",
            description = "Данный метод позволяет обновить данные о пользователе")
    public ResponseEntity<UserDto> updateUser(@Parameter(description = "id пользователя")
                                              @PathVariable Long id,
                                              @RequestBody @Valid UserDto userDto
    ) {
        User user = userMapper.toEntity(userDto);
        return new ResponseEntity<>(userMapper.toDto(userService.update(id, user)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Данный метод позволяет удалить пользователя")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "id пользователя")
                                           @PathVariable Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
