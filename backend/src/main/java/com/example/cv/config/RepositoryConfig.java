package com.example.cv.config;

import com.example.cv.analytics.repository.AnalyticsRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = AnalyticsRepository.class)
@EnableMongoRepositories(basePackages = {
        "com.example.cv.company.repository",
        "com.example.cv.job.repository",
        "com.example.cv.permission.repository",
        "com.example.cv.resume.repository",
        "com.example.cv.role.repository",
        "com.example.cv.subscriber.repository",
        "com.example.cv.user.repository"
})
public class RepositoryConfig {
}
