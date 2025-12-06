package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.users;



public interface userRepository extends JpaRepository<users, Long> {
    
}
