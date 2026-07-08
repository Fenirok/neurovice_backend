package com.project.neurovice_backend.user.service;

import org.springframework.stereotype.Service;

import com.project.neurovice_backend.auth.util.HashUtil;
import com.project.neurovice_backend.user.dto.CreateUserRequest;
import com.project.neurovice_backend.user.dto.CreateUserResponse;
import com.project.neurovice_backend.user.dto.LoginRequest;
import com.project.neurovice_backend.user.dto.LoginResponse;
import com.project.neurovice_backend.user.model.users;
import com.project.neurovice_backend.user.repository.userRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final userRepository userRepository;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailAddress(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        if (userRepository.existsByWhatsappNumber(request.getWhatsappNumber())) {
            throw new RuntimeException("User with this WhatsApp number already exists");
        }
        if (userRepository.existsByAddhaarId(request.getAadhaarId())) {
            throw new RuntimeException("User with this Aadhaar ID already exists");
        }

        users user = new users();
        user.setUsername(request.getUsername());
        user.setEmailAddress(request.getEmail());
        user.setWhatsappNumber(request.getWhatsappNumber());
        user.setAddress(request.getAddress());
        user.setRelationWithChild(request.getRelationWithChild());
        user.setAddhaarId(request.getAadhaarId());
        user.setPassword(HashUtil.hash(request.getPassword()));

        users savedUser = userRepository.save(user);

        return new CreateUserResponse(savedUser.getParentId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        users user = userRepository.findByEmailAddress(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        boolean isPasswordValid = HashUtil.verify(request.getPassword(), user.getPassword());
        if (!isPasswordValid) {
            throw new RuntimeException("Invalid password");
        }

        return new LoginResponse(user.getParentId(), user.getUsername(), user.getEmailAddress());
    }
}
