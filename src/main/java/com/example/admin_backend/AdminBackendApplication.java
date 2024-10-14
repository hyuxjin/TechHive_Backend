package com.example.admin_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class AdminBackendApplication {
    private static final Logger logger = LoggerFactory.getLogger(AdminBackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AdminBackendApplication.class, args);
        logger.info("Application started successfully!");
    }
}
