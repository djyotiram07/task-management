package com.example.repository;

import com.example.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, Long>,
        PagingAndSortingRepository<Comment, Long> {

    Page<Comment> findByTaskId(Long taskId, Pageable pageable);

    Page<Comment> findByProjectId(Long projectId, Pageable pageable);

    void deleteByProjectId(Long projectId);

    void deleteByTaskId(Long taskId);
}
