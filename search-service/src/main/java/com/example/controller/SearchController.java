package com.example.controller;

import com.example.model.IndexedProject;
import com.example.model.IndexedTask;
import com.example.model.IndexedUser;
import com.example.service.SearchService;
import com.example.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/tm-search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    // another option : advance : fuzzy search
    @GetMapping("/tasks")
    public ResponseEntity<PageResponse<IndexedTask>> searchTasks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Received search request for task.");
        Page<IndexedTask> resultPage = searchService.searchTasks(query, page, size);

        PageResponse<IndexedTask> pageResponse = new PageResponse<>(
                resultPage.getContent(),
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getSize(),
                resultPage.getNumber());

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/projects")
    public ResponseEntity<PageResponse<IndexedProject>> searchProjects(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Received search request for project.");
        Page<IndexedProject> resultPage = searchService.searchProjects(query, page, size);

        PageResponse<IndexedProject> pageResponse = new PageResponse<>(
                resultPage.getContent(),
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getSize(),
                resultPage.getNumber());

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/users")
    public ResponseEntity<PageResponse<IndexedUser>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Received search request for user.");
        Page<IndexedUser> resultPage = searchService.searchUsers(query, page, size);

        PageResponse<IndexedUser> pageResponse = new PageResponse<>(
                resultPage.getContent(),
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getSize(),
                resultPage.getNumber());

        return ResponseEntity.ok(pageResponse);
    }
}



















