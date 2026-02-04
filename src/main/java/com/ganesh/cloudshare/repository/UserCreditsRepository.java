package com.ganesh.cloudshare.repository;

import com.ganesh.cloudshare.document.userCredits;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCreditsRepository extends MongoRepository<userCredits,String> {
    // Find userCredits by clerkId
    Optional<userCredits> findByClerkId(String clerkId);
}
