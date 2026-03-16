package com.project.neurovice_backend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.child;

@Repository
public interface childRepository extends JpaRepository<child, Long> {

    List<child> findByParentId(Long parentId);

    boolean existsByAadharId(String aadharId);
}
