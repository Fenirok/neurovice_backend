package com.project.neurovice_backend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.project.neurovice_backend.auth.entity.OtpEntity;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findLatestByEmail(String email);

    @Modifying
    @Query("UPDATE OtpEntity o SET o.isUsed = true WHERE o.email = :email AND o.isUsed = false")
    void invalidateExisting(String email);

}
