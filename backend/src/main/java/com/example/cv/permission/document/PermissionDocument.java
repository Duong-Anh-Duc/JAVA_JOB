package com.example.cv.permission.document;

import com.example.cv.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document("permissions")
public class PermissionDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private AuditInfo updatedBy;
    private AuditInfo createdBy;
    private AuditInfo deletedBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean isDeleted;
    private Instant deletedAt;
}
