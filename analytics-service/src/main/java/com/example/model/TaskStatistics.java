package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_statistics")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_do")
    private Long toDo;

    @Column(name = "in_progress")
    private Long inProgress;

    @Column(name = "done")
    private Long done;

    @Column(name = "total_tasks")
    private Long totalTasks;

    @Column(name = "average_time_to_complete")
    private String averageTimeToComplete;

    @LastModifiedDate
    @Column(name = "last_updated", nullable = false, updatable = false)
    private LocalDateTime lastUpdatedAt;
}
