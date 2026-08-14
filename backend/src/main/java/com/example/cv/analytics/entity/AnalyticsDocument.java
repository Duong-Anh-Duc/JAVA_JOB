package com.example.cv.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "analytics", indexes = {
        @Index(name = "idx_analytics_event_type", columnList = "event_type"),
        @Index(name = "idx_analytics_resource_id", columnList = "resource_id"),
        @Index(name = "idx_analytics_user_id", columnList = "user_id"),
        @Index(name = "idx_analytics_created_at", columnList = "created_at")
})
public class AnalyticsDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    @Column(name = "resource_id", length = 255)
    private String resourceId;
    @Column(name = "user_id", length = 255)
    private String userId;
    @Column(name = "session_id", length = 255)
    private String sessionId;
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "json")
    private Map<String, Object> metadata;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public AnalyticsDocument(String eventType, String resourceId, String userId, String sessionId,
                             String ipAddress, String userAgent, Map<String, Object> metadata) {
        this.eventType = eventType;
        this.resourceId = resourceId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.metadata = metadata;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
}
