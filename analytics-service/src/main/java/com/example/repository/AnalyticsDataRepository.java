package com.example.repository;

import com.example.model.AnalyticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticsDataRepository extends JpaRepository<AnalyticsData, Long> {

    Optional<AnalyticsData> findByTaskId(Long taskId);
}
