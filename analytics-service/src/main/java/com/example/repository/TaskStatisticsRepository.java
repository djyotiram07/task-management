package com.example.repository;

import com.example.model.TaskStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatisticsRepository extends JpaRepository<TaskStatistics, Long> {

    @Query("SELECT ts FROM TaskStatistics ts ORDER BY ts.lastUpdatedAt DESC")
    Optional<TaskStatistics> findLatestTaskStatistics();
}
