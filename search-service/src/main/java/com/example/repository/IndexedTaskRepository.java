package com.example.repository;

import com.example.model.IndexedTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexedTaskRepository extends ElasticsearchRepository<IndexedTask, Long> {

    Page<IndexedTask> findByTitleContaining(String title, Pageable pageable);

    @Query("{\"fuzzy\": {\"title\": {\"value\": \"?0\", \"fuzziness\": 2}}}")
    Page<IndexedTask> findByTitleFuzzy(String title, Pageable pageable);

    void deleteByTaskId(Long taskId);
}
