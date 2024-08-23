package com.example.service;

import com.example.model.IndexedProject;
import com.example.model.IndexedTask;
import com.example.model.IndexedUser;
import org.springframework.data.domain.Page;

public interface SearchService {

    Page<IndexedTask> searchTasks(String query, int page, int size);

    Page<IndexedProject> searchProjects(String query, int page, int size);

    Page<IndexedUser> searchUsers(String query, int page, int size);
}
