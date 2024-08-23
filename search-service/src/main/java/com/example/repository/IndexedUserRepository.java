package com.example.repository;

import com.example.model.IndexedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexedUserRepository extends ElasticsearchRepository<IndexedUser, Long> {

    Page<IndexedUser> findByUsernameContaining(String username, Pageable pageable);

    @Query("{\"fuzzy\": {\"username\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}")
    Page<IndexedUser> findByUsernameFuzzy(String username, Pageable pageable);

    void deleteByUserId(Long userId);
}
