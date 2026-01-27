package com.ganesh.cloudshare.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks-url}")
    private String jwksUrl;

    private final Map<String, PublicKey> keyCache = new HashMap<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TTL = 60 * 60 * 1000; // 1hr

    public PublicKey getPublicKey(String kid) throws Exception {
        if(keyCache.containsKey(kid)&&System.currentTimeMillis() - lastFetchTime < CACHE_TTL) {
            return keyCache.get(kid); // if it is present in the CACHE, it will return the Keys wrt Key ID.
        }
        //if the keys are expired(more than 1hr) then refresh the key
        refreshKeys();
        return keyCache.get(kid);
    }
    private void refreshKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwks = mapper.readTree(jwksUrl); // Converts JSON to Java obj

        JsonNode keys = jwks.get("keys");
        for(JsonNode key : keys) {
            String kid = key.get("kid").asText();
            String keyType = key.get("kty").asText();
            String algorithm = key.get("alg").asText();

            if("RSA".equals(keyType) && "RS256".equals(algorithm)) {
                String n = key.get("n").asText(); //Modulus
                String e = key.get("e").asText(); // exponentialValue

                PublicKey publicKey = createPublicKey(n,e);
                keyCache.put(kid,publicKey);
            }

        }
        lastFetchTime = System.currentTimeMillis();
    }

    private PublicKey createPublicKey(String modulus, String exponent) throws Exception {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

        BigInteger modulusBigInt = new BigInteger(1, modulusBytes);
        BigInteger exponentBigInt = new BigInteger(1, exponentBytes);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusBigInt, exponentBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);// Gives RSA public key
    }
}
