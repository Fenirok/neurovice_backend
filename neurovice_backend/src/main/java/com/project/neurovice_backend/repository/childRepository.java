package com.project.neurovice_backend.repository;

import com.project.neurovice_backend.model.child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface childRepository extends JpaRepository<child, Long> {
    List<child> findByParentId(Long parentId);
    boolean existsByAadharId(String aadharId);
}
