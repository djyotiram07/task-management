package com.example.controller;

import com.example.dto.CommentDto;
import com.example.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/tm-comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<CommentDto> addCommentToProject(@PathVariable Long projectId,
                                                          @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.addCommentToProject(projectId, commentDto),
                HttpStatus.CREATED);
    }

    @PostMapping("/tasks/{taskId}")
    public ResponseEntity<CommentDto> addCommentToTask(@PathVariable Long taskId,
                                                       @RequestBody CommentDto commentDto) {
        return new ResponseEntity<>(commentService.addCommentToTask(taskId, commentDto),
                HttpStatus.CREATED);
    }

    // transactional : read only
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByTaskId(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CommentDto> comments =
                commentService.getCommentsByTaskId(taskId, page, size);
        return ResponseEntity.ok(comments);
    }

    // transactional : read only
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByProjectId(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CommentDto> comments =
                commentService.getCommentsByProjectId(projectId, page, size);
        return ResponseEntity.ok(comments);
    }
}
