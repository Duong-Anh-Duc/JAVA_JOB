package com.example.cv.resume.repository;

import com.example.cv.resume.document.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResumeRepository extends MongoRepository<ResumeDocument, String> {
    List<ResumeDocument> findAllByIsDeletedFalse();
    List<ResumeDocument> findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId);
    long countByIsDeletedFalse();
}
