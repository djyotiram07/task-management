package com.example.model;

import com.example.utils.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatuses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private String taskTitle;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TaskStatus statusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanban_board_id")
    private KanbanBoard kanbanBoard;
}
