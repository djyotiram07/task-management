package com.example.repository;

import com.example.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        PagingAndSortingRepository<Notification, Long> {

    Page<Notification> findAllByUserId(Long id, Pageable pageable);

    Optional<Notification> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
