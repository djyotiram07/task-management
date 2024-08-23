package com.example.repository;

import com.example.model.UserPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPerformanceRepository extends JpaRepository<UserPerformance, Long> {

    @Query("SELECT up FROM UserPerformance up WHERE up.userId = :userId ORDER BY up.lastUpdatedAt DESC")
    Optional<UserPerformance> findLatestByUserId(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}
