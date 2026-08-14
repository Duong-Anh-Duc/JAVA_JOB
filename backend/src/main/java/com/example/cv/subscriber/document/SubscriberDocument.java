package com.example.cv.subscriber.document;

import com.example.cv.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document("subscribers")
public class SubscriberDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String email;
    private String name;
    private List<String> skills = new ArrayList<>();
    private AuditInfo createdBy;
    private AuditInfo updatedBy;
    private AuditInfo deletedBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean isDeleted;
    private Instant deletedAt;
}
