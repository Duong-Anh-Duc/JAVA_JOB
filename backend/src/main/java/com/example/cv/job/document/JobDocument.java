package com.example.cv.job.document;

import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.model.CompanySnapshot;
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
@Document("jobs")
public class JobDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String name;
    private List<String> skills = new ArrayList<>();
    private CompanySnapshot company;
    private Double salary;
    private Integer quantity;
    private String level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive;
    private String location;
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
