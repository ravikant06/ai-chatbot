package com.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Chatbot Service
 * 
 * This is the entry point of our Spring Boot application.
 * 
 * @SpringBootApplication annotation combines three important annotations:
 * 1. @Configuration: Tells Spring this class can define beans (objects managed by Spring)
 * 2. @EnableAutoConfiguration: Automatically configures Spring based on dependencies in classpath
 * 3. @ComponentScan: Scans current package and sub-packages for Spring components
 * 
 * How Spring Boot works:
 * 1. This main method starts the application
 * 2. Spring scans for @Controller, @Service, @Repository classes
 * 3. Creates web server (Tomcat) on port 8080 by default
 * 4. Sets up all endpoints, database connections, etc.
 */
@SpringBootApplication
public class ChatbotApplication {

    /**
     * Main method - Entry point of the application
     * 
     * @param args Command line arguments (e.g., --server.port=8081)
     * 
     * SpringApplication.run() does:
     * 1. Creates Spring application context (container for all beans)
     * 2. Starts embedded Tomcat server
     * 3. Initializes all @Controller, @Service, @Repository classes
     * 4. Connects to databases
     * 5. Makes the application ready to receive HTTP requests
     */
    public static void main(String[] args) {
        // Equivalent to: new SpringApplication(ChatbotApplication.class).run(args);
        SpringApplication.run(ChatbotApplication.class, args);
    }
}