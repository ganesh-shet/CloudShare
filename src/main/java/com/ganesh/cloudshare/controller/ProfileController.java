package com.ganesh.cloudshare.controller;

import com.ganesh.cloudshare.DTO.ProfileDTO;
import com.ganesh.cloudshare.document.ProfileDocument;
import com.ganesh.cloudshare.repository.ProfileRepository;
import com.ganesh.cloudshare.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @PostMapping("/registerProfile")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileDTO profileDTO) {
        HttpStatus status = profileService.existsByClerkId(profileDTO.getClerkId()) ? HttpStatus.OK : HttpStatus.CREATED;
        ProfileDTO savedProfile = profileService.createProfile(profileDTO);
        return ResponseEntity.status(status).body(savedProfile);
    }

    @DeleteMapping("delete/{clerkId}")
    public ResponseEntity<String> deleteProfile(@PathVariable String clerkId) {
        boolean deleted = profileService.deleteProfile(clerkId);
        if (deleted) {
            return ResponseEntity.ok("Profile deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Profile not found with clerkId: " + clerkId);
        }
    }

}
