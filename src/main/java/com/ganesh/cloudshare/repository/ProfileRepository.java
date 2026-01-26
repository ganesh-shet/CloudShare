package com.ganesh.cloudshare.repository;

import com.ganesh.cloudshare.document.ProfileDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {
   Optional<ProfileDocument> findByEmail(String email);

   ProfileDocument findByClerkId(String clerkId);

   Boolean existsByClerkId(String clerkId);
}
