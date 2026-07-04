package com.project.neurovice_backend.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "whatsapp_number", nullable = false, unique = true)
    private String whatsappNumber;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "relation_with_child", nullable = false, length = 20)
    private String relationWithChild;

    @Column(name = "addhaar_id", nullable = false, unique = true)
    private String addhaarId;
}
