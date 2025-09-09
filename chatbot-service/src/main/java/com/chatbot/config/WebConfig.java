package com.chatbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration - CORS and HTTP Settings
 * 
 * Configuration Class Explanation:
 * - @Configuration tells Spring this class contains configuration
 * - Runs at startup to configure Spring components
 * - Alternative to XML configuration files
 * 
 * CORS (Cross-Origin Resource Sharing) Problem:
 * - Browser security prevents React (localhost:5173) from calling Spring Boot (localhost:8081)
 * - Different ports = different origins = blocked by browser
 * - We need to explicitly allow cross-origin requests
 * 
 * WebMvcConfigurer interface:
 * - Provides hooks to customize Spring MVC behavior
 * - We override addCorsMappings to allow React frontend access
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS (Cross-Origin Resource Sharing)
     * 
     * This method runs at application startup
     * Tells Spring which origins can access our APIs
     * 
     * Without this configuration:
     * React app -> Browser blocks request -> Error: "CORS policy error"
     * 
     * With this configuration:  
     * React app -> Browser allows request -> Spring Boot API
     * 
     * @param registry CORS configuration registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        /*
         * CORS Configuration breakdown:
         * 
         * addMapping("/api/**") - Apply CORS rules to all URLs starting with /api/
         * allowedOrigins() - Which domains can access our APIs
         * allowedMethods() - Which HTTP methods are allowed
         * allowedHeaders() - Which HTTP headers are allowed
         * allowCredentials() - Allow cookies/auth headers
         */
        
        registry.addMapping("/api/**")                    // Apply to all /api/* endpoints
                .allowedOrigins(
                    "http://localhost:5173",             // React development server (Vite)
                    "http://localhost:3001",             // Alternative React port
                    "http://localhost:3000"              // Create React App default port
                )
                .allowedMethods(
                    "GET",                               // Read data
                    "POST",                              // Create data  
                    "PUT",                               // Update data
                    "DELETE",                            // Remove data
                    "OPTIONS"                            // CORS preflight requests
                )
                .allowedHeaders("*")                     // Allow all headers
                .allowCredentials(true)                  // Allow cookies and authorization headers
                .maxAge(3600);                          // Cache CORS preflight for 1 hour
        
        /*
         * LAMBDA EQUIVALENT (if CORS configuration used lambdas):
         * 
         * List<String> allowedOrigins = List.of("http://localhost:5173", "http://localhost:3001");
         * List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
         * 
         * allowedOrigins.forEach(origin -> 
         *     allowedMethods.forEach(method -> 
         *         registry.addMapping("/api/**")
         *             .allowedOrigins(origin)
         *             .allowedMethods(method)
         *     )
         * );
         * 
         * But the builder pattern above is much cleaner!
         */
    }

    /*
     * WHAT HAPPENS WITHOUT CORS CONFIGURATION:
     * 
     * 1. React app tries to call: fetch("http://localhost:8081/api/chat/send", {...})
     * 2. Browser checks: "localhost:5173 trying to access localhost:8081"
     * 3. Browser blocks: "Cross-origin request blocked by CORS policy"
     * 4. Frontend gets error: "Network Error" or "CORS Error"
     * 
     * WITH CORS CONFIGURATION:
     * 
     * 1. React app tries to call: fetch("http://localhost:8081/api/chat/send", {...})
     * 2. Browser checks CORS headers from Spring Boot
     * 3. Browser sees localhost:5173 is allowed
     * 4. Request succeeds, frontend gets response
     * 
     * CORS is a browser security feature, not a server security feature!
     */
}