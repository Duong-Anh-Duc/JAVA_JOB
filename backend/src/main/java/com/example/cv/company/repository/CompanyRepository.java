package com.example.cv.company.repository;

import com.example.cv.company.document.CompanyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompanyRepository extends MongoRepository<CompanyDocument, String> {
    List<CompanyDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
