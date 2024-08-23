package com.example.repository;

import com.example.model.ProjectProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectProgressRepository extends JpaRepository<ProjectProgress, Long> {

    @Query("SELECT pp FROM ProjectProgress pp WHERE pp.projectId = :projectId ORDER BY pp.lastUpdatedAt DESC")
    Optional<ProjectProgress> findLatestByProjectId(@Param("projectId") Long projectId);

    void deleteByProjectId(Long projectId);
}
