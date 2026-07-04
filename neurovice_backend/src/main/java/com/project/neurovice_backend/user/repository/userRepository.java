package com.project.neurovice_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.neurovice_backend.user.model.users;

public interface userRepository extends JpaRepository<users, Long> {

    boolean existsByEmailAddress(String email);
    
    boolean existsByWhatsappNumber(String whatsappNumber);
    
    boolean existsByAddhaarId(String aadhaarId);
}
