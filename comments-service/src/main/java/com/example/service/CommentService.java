package com.example.service;

import com.example.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {

    CommentDto addCommentToTask(Long taskId, CommentDto commentDto);

    Page<CommentDto> getCommentsByTaskId(Long id, int page, int size);

    CommentDto addCommentToProject(Long id, CommentDto commentDto);

    Page<CommentDto> getCommentsByProjectId(Long id, int page, int size);
}
