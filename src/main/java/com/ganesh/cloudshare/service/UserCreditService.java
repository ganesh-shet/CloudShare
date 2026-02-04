package com.ganesh.cloudshare.service;

import com.ganesh.cloudshare.document.userCredits;
import com.ganesh.cloudshare.repository.UserCreditsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {

    private final UserCreditsRepository userCreditsRepository;
    private final ProfileService profileService;

    public userCredits createInitialCredits(String clerkId) {
        userCredits credits = userCredits.builder()
                .clerkId(clerkId)
                .credits(5)
                .plan("BASIC")
                .build();

        return userCreditsRepository.save(credits);
    }

    //Get credits for a specific user by clerkId
    public userCredits getUserCredits(String clerkId) {
        return userCreditsRepository.findByClerkId(clerkId)
                .orElseGet(() -> createInitialCredits(clerkId));
    }

    //Get credits for current logged in user
    public userCredits getUserCredits(){
        String clerkId = profileService.getCurrentProfile().getClerkId();
        return getUserCredits(clerkId);
    }

    //Check how many credits user has and whether he has enough credits to perform an action
    public Boolean hasEnoughCredits(int requiredCredits){
        userCredits credits = getUserCredits();
        return credits.getCredits() >= requiredCredits;
    }
}
