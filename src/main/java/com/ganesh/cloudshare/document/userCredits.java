package com.ganesh.cloudshare.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Document(collection = "user_credits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class userCredits {

    @Id
    private String id;

    private String clerkId;
    private Integer credits;
    private String plan; //Basic, Premium, Ultimate
}
