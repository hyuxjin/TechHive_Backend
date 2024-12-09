package com.example.admin_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get and verify upload directory for the admin frontend
        String uploadDir = System.getProperty("user.home") + 
            "/Documents/GitHub/ADMIN_TECHHIVE_FRONTEND/public/Upload_report";
        File directory = new File(uploadDir);

        // Print debug information
        System.out.println("Upload Directory Path: " + uploadDir);
        System.out.println("Directory exists: " + directory.exists());
        System.out.println("Is directory: " + directory.isDirectory());
        System.out.println("Can read: " + directory.canRead());
        System.out.println("Can write: " + directory.canWrite());

        // Create directory if it doesn't exist
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Directory created: " + created);
        }

        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        System.out.println("Absolute Path: " + uploadAbsolutePath);

        // Configure resource handler for Upload_report directory
        registry.addResourceHandler("/Upload_report/**")
            .addResourceLocations("file:" + uploadAbsolutePath + File.separator)
            .setCachePeriod(3600)
            .resourceChain(true);

        // Configure resource handler for static resources
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
            .resourceChain(true);

        // Configure resource handler for uploads
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + System.getProperty("user.home") + "/thetechhive_uploads/")
            .setCachePeriod(3600)
            .resourceChain(true);

        // Additional debug information
        System.out.println("Resource handlers configured:");
        System.out.println("- /Upload_report/** -> " + uploadAbsolutePath);
        System.out.println("- /static/** -> classpath:/static/");
        System.out.println("- /uploads/** -> " + System.getProperty("user.home") + "/thetechhive_uploads/");
    }
}