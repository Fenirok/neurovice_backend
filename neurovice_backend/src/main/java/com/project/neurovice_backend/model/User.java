package com.project.neurovice_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parent_id;

    @Column(name = "user_name", nullable = false)
    private String username;
    
    @Column(name = "email_address", nullable = false, unique = true)
    private String email_address;

    @Column(name = "whatsapp_number", nullable = false, unique = true)
    private String whatsapp_number;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "addhaar_id", nullable = false, unique = true)
    private String addhaar_id;
}
