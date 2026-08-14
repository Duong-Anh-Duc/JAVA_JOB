package com.example.cv.resume.document;

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
@Document("resumes")
public class ResumeDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String email;
    private String userId;
    private String url;
    private String status;
    private String companyId;
    private String jobId;
    private List<ResumeHistory> history = new ArrayList<>();
    private AuditInfo createdBy;
    private AuditInfo updatedBy;
    private AuditInfo deleteBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean isDeleted;
    private Instant deletedAt;
}
