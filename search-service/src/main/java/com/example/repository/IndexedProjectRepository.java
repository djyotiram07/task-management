package com.example.repository;

import com.example.model.IndexedProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexedProjectRepository extends ElasticsearchRepository<IndexedProject, Long> {

    Page<IndexedProject> findByTitleContaining(String title, Pageable pageable);

    @Query("{\"fuzzy\": {\"title\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}")
    Page<IndexedProject> findByTitleFuzzy(String title, Pageable pageable);

    void deleteByProjectId(Long projectId);
}
