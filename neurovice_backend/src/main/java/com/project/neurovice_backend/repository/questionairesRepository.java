package com.project.neurovice_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.neurovice_backend.model.questionaires;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface questionairesRepository extends JpaRepository<questionaires, Long> {
    List<questionaires> findAllByIsActiveTrueOrderByDisplayOrder();
    Optional<questionaires> findBySectionId(String sectionId);
}
