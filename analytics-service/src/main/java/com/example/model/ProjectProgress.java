package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_progress")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProjectProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "tasks_completed")
    private Long tasksCompleted;

    @Column(name = "total_tasks")
    private Long totalTasks;

    @Column(name = "completion_percentage")
    private BigDecimal completionPercentage;

    @Column(name = "estimated_completion_date")
    private LocalDateTime estimatedCompletionDate;

    @LastModifiedDate
    @Column(name = "last_updated", nullable = false, updatable = false)
    private LocalDateTime lastUpdatedAt;
}

