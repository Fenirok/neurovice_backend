package com.project.neurovice_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.neurovice_backend.user.model.ADHDAnalysisResult;

@Repository
public interface ADHDAnalysisRepository extends JpaRepository<ADHDAnalysisResult, Long> {

}
