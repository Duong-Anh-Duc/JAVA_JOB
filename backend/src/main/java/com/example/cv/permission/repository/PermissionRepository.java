package com.example.cv.permission.repository;

import com.example.cv.permission.document.PermissionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PermissionRepository extends MongoRepository<PermissionDocument, String> {
    List<PermissionDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
