package com.example.cv.job.repository;

import com.example.cv.job.document.JobDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobRepository extends MongoRepository<JobDocument, String> {
    List<JobDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
