package com.project.neurovice_backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.questionaires;

@Repository
public interface questionairesRepository extends JpaRepository<questionaires, Long> {

    List<questionaires> findAllByIsActiveTrueOrderByDisplayOrder();

    Optional<questionaires> findBySectionId(String sectionId);

    int countByIsActiveTrue();
}
