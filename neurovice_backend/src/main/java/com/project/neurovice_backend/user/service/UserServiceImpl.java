package com.project.neurovice_backend.user.service;

import org.springframework.stereotype.Service;

import com.project.neurovice_backend.user.dto.CreateUserRequest;
import com.project.neurovice_backend.user.dto.CreateUserResponse;
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

        users savedUser = userRepository.save(user);

        return new CreateUserResponse(savedUser.getParentId());
    }
}
