package com.example.cv.analytics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.cv.analytics.entity.AnalyticsDocument;

import java.time.Instant;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<AnalyticsDocument, Long> {
    @Query("select a from AnalyticsDocument a where (:eventType is null or a.eventType = :eventType) " +
            "and (:userId is null or a.userId = :userId) and (:resourceId is null or a.resourceId = :resourceId) " +
            "and (:startDate is null or a.createdAt >= :startDate) and (:endDate is null or a.createdAt <= :endDate)")
    Page<AnalyticsDocument> search(@Param("eventType") String eventType, @Param("userId") String userId,
                                   @Param("resourceId") String resourceId, @Param("startDate") Instant startDate,
                                   @Param("endDate") Instant endDate, Pageable pageable);

    @Query(value = "select event_type as event_type, count(*) as count from analytics group by event_type order by count desc", nativeQuery = true)
    List<Object[]> statsByEventType();

    @Query(value = "select cast(created_at as date) as date, count(*) as count from analytics where created_at >= :startDate group by cast(created_at as date) order by date asc", nativeQuery = true)
    List<Object[]> dailyStats(@Param("startDate") Instant startDate);

    @Query(value = "select user_id as user_id, count(*) as count from analytics where user_id is not null group by user_id order by count desc limit :limit", nativeQuery = true)
    List<Object[]> topUsers(@Param("limit") int limit);
}
