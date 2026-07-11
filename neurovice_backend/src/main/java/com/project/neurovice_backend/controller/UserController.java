package com.project.neurovice_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.neurovice_backend.user.dto.CreateUserRequest;
import com.project.neurovice_backend.user.dto.CreateUserResponse;
import com.project.neurovice_backend.user.dto.LoginRequest;
import com.project.neurovice_backend.user.dto.LoginResponse;
import com.project.neurovice_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
