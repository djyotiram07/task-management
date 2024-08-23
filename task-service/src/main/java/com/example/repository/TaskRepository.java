package com.example.repository;

import com.example.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {

    Page<Task> findAllByUserId(Long id, Pageable pageable);

    void deleteByUserId(Long userId);

    void deleteByProjectId(Long projectId);

    boolean existsByProjectId(Long projectId);

    Task findByProjectId(Long projectId);

    boolean existsByUserId(Long userId);

    Task findByUserId(Long userId);
}
