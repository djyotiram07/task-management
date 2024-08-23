package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "task")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexedTask {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long taskId;

    @Field(type = FieldType.Text, analyzer = "custom_fuzzy_analyzer")
    private String title;

    @CreatedDate
    @Field(type = FieldType.Date)
    private Instant createdAt;

    @LastModifiedDate
    @Field(type = FieldType.Date)
    private Instant updatedAt;
}
