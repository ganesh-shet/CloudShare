package com.ganesh.cloudshare.service;

import com.ganesh.cloudshare.document.userCredits;
import com.ganesh.cloudshare.repository.UserCreditsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditService {

    private final UserCreditsRepository userCreditsRepository;

    public userCredits createInitialCredits(String clerkId) {
        userCredits credits = userCredits.builder()
                .clerkId(clerkId)
                .credits(5)
                .plan("BASIC")
                .build();

        return userCreditsRepository.save(credits);
    }
}
