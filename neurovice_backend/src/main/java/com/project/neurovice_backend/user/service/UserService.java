package com.project.neurovice_backend.user.service;

import com.project.neurovice_backend.user.dto.CreateUserRequest;
import com.project.neurovice_backend.user.dto.CreateUserResponse;
import com.project.neurovice_backend.user.dto.LoginRequest;
import com.project.neurovice_backend.user.dto.LoginResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    LoginResponse login(LoginRequest request);
}
