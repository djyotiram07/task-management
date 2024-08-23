package com.example.repository;

import com.example.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>,
        PagingAndSortingRepository<Project, Long> {

    Page<Project> findAllByUserId(Long id, Pageable pageable);
}
