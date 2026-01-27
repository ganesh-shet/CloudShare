package com.ganesh.cloudshare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJWTAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider clerkJwksProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //No Authentication needed for Webhook urls
        if(request.getRequestURI().contains("/webhooks")) {
            filterChain.doFilter(request, response);
        }
        String authorizationHeader = request.getHeader("Authorization");
        //if AuthHeader is missing
        if(authorizationHeader == null && !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized/AuthHeader Missing!!");
            return;
        }
        //if AuthHeader is present
        try{
            String token = authorizationHeader.substring(7);
            String[] chunks = token.split("\\.");
            //JWT will have 3 chunks -- Header[0]
            if(chunks.length < 3) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token!");
                return;
            }

            //Checking the Header
            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson); //Convert to JAVA object

            //If key Id is not there in Header
            if(!headerNode.has("kid")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Header Missing kid!");
                return;
            }
            String kid = headerNode.get("kid").asText();
            //Generate PublicKey
            PublicKey publicKey = clerkJwksProvider.getPublicKey("kid");

            //verify token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String clerkId = claims.getSubject();
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(clerkId,null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            filterChain.doFilter(request, response);

        }catch(Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token!: " + e.getMessage());
            return;
        }



    }
}
