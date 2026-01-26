package com.ganesh.cloudshare.service;

import com.ganesh.cloudshare.DTO.ProfileDTO;
import com.ganesh.cloudshare.document.ProfileDocument;
import com.ganesh.cloudshare.repository.ProfileRepository;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileDTO createProfile(ProfileDTO profileDTO){

        //if clerkId/user already exists, it goes to update profile, else it creates new user
        if(profileRepository.existsByClerkId(profileDTO.getClerkId())){
            return updateProfile(profileDTO);
        }

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

    public ProfileDTO updateProfile(ProfileDTO profileDTO) {
        ProfileDocument exisitingProfile = profileRepository.findByClerkId(profileDTO.getClerkId());
        if (exisitingProfile != null) {
            if(profileDTO.getEmail() !=null && !profileDTO.getEmail().isEmpty()){
                exisitingProfile.setEmail(profileDTO.getEmail());
            }
            if(profileDTO.getFirstName() !=null && !profileDTO.getFirstName().isEmpty()){
                exisitingProfile.setFirstName(profileDTO.getFirstName());
            }
            if(profileDTO.getLastName() !=null && !profileDTO.getLastName().isEmpty()){
                exisitingProfile.setLastName(profileDTO.getLastName());
            }
            if(profileDTO.getPhotoUrl() !=null && !profileDTO.getPhotoUrl().isEmpty()){
                exisitingProfile.setPhotoUrl(profileDTO.getPhotoUrl());
            }

            else{
                throw new RuntimeException();
            }

            profileRepository.save(exisitingProfile);

            //convert to Profile DTO
            return ProfileDTO.builder()
                    .id(exisitingProfile.getId())
                    .clerkId(exisitingProfile.getClerkId())
                    .email(exisitingProfile.getEmail())
                    .firstName(exisitingProfile.getFirstName())
                    .lastName(exisitingProfile.getLastName())
                    .credits(exisitingProfile.getCredits())
                    .photoUrl(exisitingProfile.getPhotoUrl())
                    .createdAt(exisitingProfile.getCreatedAt())
                    .build();
        } return null;
    }
    public Boolean existsByClerkId(String clerkId) {
        return profileRepository.existsByClerkId(clerkId);
    }

    public boolean deleteProfile(String clerkId) {
        ProfileDocument profile = profileRepository.findByClerkId(clerkId);
        if (profile != null) {
            profileRepository.delete(profile);
            return true;
        }
        return false;
    }

}
