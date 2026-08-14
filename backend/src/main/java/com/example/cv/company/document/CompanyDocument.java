package com.example.cv.company.document;

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
@Document("companies")
public class CompanyDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String name;
    private String address;
    private String description;
    private String logo;
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
