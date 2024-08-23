package com.example.repository;

import com.example.model.TaskStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatuses, Long> {

    void deleteByTaskId(Long id);
}
