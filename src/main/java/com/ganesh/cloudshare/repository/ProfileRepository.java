package com.ganesh.cloudshare.repository;

import com.ganesh.cloudshare.document.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {
   // Find profile by email
   Optional<ProfileDocument> findByEmail(String email);

   // Find profile by clerkId
   ProfileDocument findByClerkId(String clerkId);

    // Check if profile exists by clerkId
   Boolean existsByClerkId(String clerkId);
}
