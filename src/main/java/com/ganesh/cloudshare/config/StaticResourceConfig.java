package com.ganesh.cloudshare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    // Configure resource handlers to serve files from the "uploads" directory
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the "uploads" directory
        String uploadDir = Paths.get("uploads").toAbsolutePath().toString();
        // Map URL path "/uploads/**" to the physical directory
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir+"/");
    }
}
