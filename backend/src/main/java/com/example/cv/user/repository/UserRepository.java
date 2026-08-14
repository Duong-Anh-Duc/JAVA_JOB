package com.example.cv.user.repository;

import com.example.cv.user.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmailAndIsDeletedFalse(String email);
    Optional<UserDocument> findByRefreshTokenAndIsDeletedFalse(String refreshToken);
    List<UserDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
