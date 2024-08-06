package com.mrv.auction.controller;

import com.mrv.auction.dto.user.UserDto;
import com.mrv.auction.mappers.UserMapper;
import com.mrv.auction.model.User;
import com.mrv.auction.service.UserService;
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
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @RequestBody @Valid UserDto userDto
    ) {
        User user = userMapper.toEntity(userDto);
        return new ResponseEntity<>(userMapper.toDto(userService.update(id, user)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
