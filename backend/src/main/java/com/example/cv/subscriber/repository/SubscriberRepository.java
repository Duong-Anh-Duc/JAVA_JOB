package com.example.cv.subscriber.repository;

import com.example.cv.subscriber.document.SubscriberDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends MongoRepository<SubscriberDocument, String> {
    Optional<SubscriberDocument> findByEmailAndIsDeletedFalse(String email);
    List<SubscriberDocument> findAllByIsDeletedFalse();
    long countByIsDeletedFalse();
}
