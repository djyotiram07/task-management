package com.example.service;

import com.example.client.ProjectServiceProxy;
import com.example.client.TaskServiceProxy;
import com.example.dto.CommentDto;
import com.example.dto.ProjectDto;
import com.example.dto.TaskDto;
import com.example.event.ProjectDeleteEvent;
import com.example.event.TaskDeleteEvent;
import com.example.exceptions.CommentCommonException;
import com.example.mapper.CommentMapper;
import com.example.model.Comment;
import com.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskServiceProxy taskServiceProxy;
    private final ProjectServiceProxy projectServiceProxy;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addCommentToTask(Long taskId, CommentDto commentDto) {

        try {

            TaskDto taskDto = taskServiceProxy.findTaskById(taskId);

            if (taskDto == null) {
                throw new CommentCommonException("Task not found.");
            }

            Comment comment = commentMapper.commentDtoToComment(commentDto);
            Comment savedComment = commentRepository.save(comment);
            return commentMapper.commentToCommentDto(savedComment);

        } catch (Exception e) {
            throw new CommentCommonException("Error while adding comment.");
        }
    }

    @Override
    public CommentDto addCommentToProject(Long id, CommentDto commentDto) {
        try {
            ProjectDto projectDto = projectServiceProxy.findProjectById(id);

            if (projectDto == null) {
                throw new CommentCommonException("Project not found.");
            }

            Comment comment = commentMapper.commentDtoToComment(commentDto);
            Comment savedComment = commentRepository.save(comment);
            return commentMapper.commentToCommentDto(savedComment);

        } catch (Exception e) {
            throw new CommentCommonException("Error while adding comment.");
        }
    }

    @Override
    public Page<CommentDto> getCommentsByTaskId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return commentRepository.findByTaskId(id, pageable).map(commentMapper::commentToCommentDto);
    }

    @Override
    public Page<CommentDto> getCommentsByProjectId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return commentRepository.findByProjectId(id, pageable).map(commentMapper::commentToCommentDto);
    }

    @KafkaListener(topics = "deleteTask")
    public void taskDeleteListener(TaskDeleteEvent taskDeleteEvent) {
        log.info("Received delete message from task service: {}", taskDeleteEvent);

        try {
            commentRepository.deleteByTaskId(taskDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete task with ID {}: {}", taskDeleteEvent.id(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteProject")
    public void projectDeleteListener(ProjectDeleteEvent projectDeleteEvent) {
        log.info("Received delete message from project service: {}", projectDeleteEvent);

        try {
            commentRepository.deleteByProjectId(projectDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete project with ID {}: {}", projectDeleteEvent.id(), e.getMessage());
        }
    }
}
