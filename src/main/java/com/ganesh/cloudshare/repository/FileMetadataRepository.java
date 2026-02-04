package com.ganesh.cloudshare.repository;

import com.ganesh.cloudshare.document.FileMetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataRepository extends MongoRepository<FileMetadataDocument, String> {
    // Find all files by clerkId
    List<FileMetadataDocument> findByClerkId(String clerkId);

    // Count files by clerkId
    Long countByClerkId(String clerkId);
}
