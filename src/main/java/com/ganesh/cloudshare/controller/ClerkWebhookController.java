package com.ganesh.cloudshare.controller;

import com.ganesh.cloudshare.DTO.ProfileDTO;
import com.ganesh.cloudshare.service.ProfileService;
import com.ganesh.cloudshare.service.UserCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class ClerkWebhookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final ProfileService profileService;
    private final UserCreditService userCreditService;

    @PostMapping("/clerk")
    public ResponseEntity<?>handleClerkWebhook(@RequestHeader("svix-id") String svixid,
                                               @RequestHeader("svix-timestamp") String svixTimestamp,
                                               @RequestHeader("svix-signature") String svixsignature,
                                               @RequestBody String payload) {
            try{
                boolean isValid = verifyWebhookSignature(svixid,svixTimestamp,svixsignature,payload);
                if(!isValid){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid Webhook Signature");
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(payload);
                String eventType = rootNode.path("type").asText(); //user_created/user_updated/user_deleted

                switch (eventType){
                    case "user.created":
                        handleUserCreated(rootNode.path("data"));
                        break;
                    case "user.updated":
                        handleUserUpdated(rootNode.path("data"));
                        break;
                    case "user.deleted":
                        handleUserDeleted(rootNode.path("data"));
                        break;
                }
                return ResponseEntity.status(HttpStatus.OK).build();
            }catch(Exception e){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());

            }

    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();

        profileService.deleteProfile(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();
        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");
        if(emailAddresses.isArray() && emailAddresses.size() >0){
            email = emailAddresses.get(0).path("email_address").asText();
        }
        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String photoUrl = data.path("image_url").asText("");

        ProfileDTO updatedProfile = ProfileDTO.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();

        updatedProfile = profileService.updateProfile(updatedProfile);
        if(updatedProfile == null){
            handleUserCreated(data);
        }
    }

    private void handleUserCreated(JsonNode data) {
        String clerkId = data.path("id").asText();
        String email = "";
        JsonNode emailAddresses = data.path("email_address");
        if(emailAddresses.isArray() && !emailAddresses.isEmpty()){
            email = emailAddresses.get(0).path("email_address").asText();
        }
        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String photoUrl = data.path("image_url").asText("");

        ProfileDTO newProfile = ProfileDTO.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();

        profileService.createProfile(newProfile);
        userCreditService.createInitialCredits(clerkId);
    }

    private boolean verifyWebhookSignature(String svixid, String svixTimestamp, String svixsignature, String payload) {
        //validate signature
        return true; // always returns true --> can be implemented using different methods to verify the signature validations
    }
}
