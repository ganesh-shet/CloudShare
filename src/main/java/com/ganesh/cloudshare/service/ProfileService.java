package com.ganesh.cloudshare.service;

import com.ganesh.cloudshare.DTO.ProfileDTO;
import com.ganesh.cloudshare.document.ProfileDocument;
import com.ganesh.cloudshare.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        // Implementation goes here
        // Takes user's input and saves the Profile Document in DB.
        ProfileDocument profile = ProfileDocument.builder()
                .clerkId(profileDTO.getClerkId())
                .email(profileDTO.getEmail())
                .firstName(profileDTO.getFirstName())
                .lastName(profileDTO.getLastName())
                .photoUrl(profileDTO.getPhotoUrl())
                .credits(5) // Default credits
                .createdAt(Instant.now())
                .build();
        profile = profileRepository.save(profile);

        // Returns the Profile DTO back to User.
        return ProfileDTO.builder()
                .id(profile.getId())
                .clerkId(profile.getClerkId())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .photoUrl(profile.getPhotoUrl())
                .credits(profile.getCredits()) // Default credits
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
