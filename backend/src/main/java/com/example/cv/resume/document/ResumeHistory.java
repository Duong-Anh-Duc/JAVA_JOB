package com.example.cv.resume.document;

import com.example.cv.common.model.AuditInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeHistory {
    private String status;
    private Instant updatedAt;
    private AuditInfo updatedBy;
}
