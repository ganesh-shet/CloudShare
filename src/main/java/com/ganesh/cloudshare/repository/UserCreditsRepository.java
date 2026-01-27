package com.ganesh.cloudshare.repository;

import com.ganesh.cloudshare.document.userCredits;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCreditsRepository extends MongoRepository<userCredits,String> {

}
