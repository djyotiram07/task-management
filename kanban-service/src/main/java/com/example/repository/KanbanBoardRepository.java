package com.example.repository;

import com.example.model.KanbanBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KanbanBoardRepository extends JpaRepository<KanbanBoard, Long> {

    KanbanBoard findByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
