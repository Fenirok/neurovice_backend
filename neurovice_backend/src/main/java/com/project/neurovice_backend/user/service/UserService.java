package com.project.neurovice_backend.user.service;

import com.project.neurovice_backend.user.dto.CreateUserRequest;
import com.project.neurovice_backend.user.dto.CreateUserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
}
