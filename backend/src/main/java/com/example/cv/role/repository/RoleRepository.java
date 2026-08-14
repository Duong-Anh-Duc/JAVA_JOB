package com.example.cv.role.repository;

import com.example.cv.role.document.RoleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<RoleDocument, String> {
    Optional<RoleDocument> findByNameAndIsDeletedFalse(String name);
    Optional<RoleDocument> findByIdAndIsDeletedFalse(String id);
    List<RoleDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
