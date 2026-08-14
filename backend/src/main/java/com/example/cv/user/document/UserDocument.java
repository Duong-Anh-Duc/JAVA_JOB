package com.example.cv.user.document;

import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.model.CompanySnapshot;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Document("users")
public class UserDocument {
    @Id
    @JsonProperty("_id")
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private Integer age;
    private String gender;
    private String address;
    private CompanySnapshot company;
    private String role;
    @JsonIgnore
    private String refreshToken;
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
