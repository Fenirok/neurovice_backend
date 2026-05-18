package com.project.neurovice_backend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.neurovice_backend.auth.entity.OtpEntity;

import jakarta.transaction.Transactional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findTopByEmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("UPDATE OtpEntity o SET o.isUsed = true WHERE o.email = :email AND o.isUsed = false")
    void invalidateExisting(@Param("email") String email);

}
