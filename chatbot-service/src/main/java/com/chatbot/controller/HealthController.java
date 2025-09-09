package com.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Health Controller - System Status Monitoring
 * 
 * This controller provides health check endpoints to monitor:
 * - Application status
 * - Database connectivity  
 * - External service availability
 * 
 * Used by:
 * - Load balancers (to know if instance is healthy)
 * - Monitoring systems (alerts if service is down)
 * - Frontend (to show connection status)
 * - DevOps tools (deployment health checks)
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * DataSource - Low-level H2 database operations
     * 
     * While Repository is high-level (automatic methods),
     * DataSource is low-level (manual operations)
     * 
     * Used here to test database connectivity
     * We inject it to ping H2 and check if it's responding
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Main health check endpoint
     * 
     * URL: GET /health
     * 
     * Returns overall application health status
     * Frontend can call this to show "Connected" or "Disconnected" status
     * 
     * @return ResponseEntity<Map> health status and details
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        
        // HashMap allows us to build response dynamically
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test database connectivity
            // dataSource.getConnection() tries to connect to H2 database
            // If successful, H2 is working properly
            dataSource.getConnection().close();
            
            // If we reach here, H2 is working
            health.put("status", "UP");
            health.put("database", "Connected");
            health.put("timestamp", java.time.LocalDateTime.now().toString());
            health.put("service", "chatbot-service");
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            // H2 connection failed
            health.put("status", "DOWN");
            health.put("database", "Disconnected");
            health.put("error", e.getMessage());
            health.put("timestamp", java.time.LocalDateTime.now().toString());
            
            // Return HTTP 503 Service Unavailable
            return ResponseEntity.status(503).body(health);
        }
        
        /*
         * LAMBDA EQUIVALENT:
         * 
         * return Optional.of(mongoTemplate)
         *     .map(template -> {
         *         try {
         *             template.getConnection().isValid(5);
         *             return Map.of(
         *                 "status", "UP",
         *                 "database", "Connected",
         *                 "timestamp", LocalDateTime.now().toString()
         *             );
         *         } catch (Exception e) {
         *             return Map.of(
         *                 "status", "DOWN", 
         *                 "database", "Disconnected",
         *                 "error", e.getMessage()
         *             );
         *         }
         *     })
         *     .map(healthMap -> 
         *         healthMap.get("status").equals("UP") 
         *             ? ResponseEntity.ok(healthMap)
         *             : ResponseEntity.status(503).body(healthMap)
         *     )
         *     .orElse(ResponseEntity.internalServerError().build());
         */
    }

    /**
     * Detailed health check with component status
     * 
     * URL: GET /health/detailed
     * 
     * Provides detailed status of each system component
     * Useful for debugging and monitoring
     * 
     * @return ResponseEntity<Map> detailed component health
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> components = new HashMap<>();
        
        // Check H2 Database
        try {
            dataSource.getConnection().close();
            components.put("h2database", Map.of(
                "status", "UP",
                "details", "Connection successful"
            ));
        } catch (Exception e) {
            components.put("h2database", Map.of(
                "status", "DOWN", 
                "details", e.getMessage()
            ));
        }
        
        // Check AWS Bedrock (basic check - we'll add this in BedrockService later)
        components.put("bedrock", Map.of(
            "status", "UNKNOWN",
            "details", "AWS Bedrock check not implemented yet"
        ));
        
        // Overall status
        boolean allHealthy = components.values().stream()
            .allMatch(component -> {
                // Cast to Map to access status
                @SuppressWarnings("unchecked")
                Map<String, Object> comp = (Map<String, Object>) component;
                return "UP".equals(comp.get("status"));
            });
        
        /*
         * LAMBDA EXPLANATION for allHealthy check:
         * 
         * .stream() - Convert collection to stream for processing
         * .allMatch() - Returns true if ALL elements match condition
         * .allMatch(predicate) - predicate is a lambda function that returns boolean
         * 
         * Alternative without lambda:
         * boolean allHealthy = true;
         * for (Object component : components.values()) {
         *     Map<String, Object> comp = (Map<String, Object>) component;
         *     if (!"UP".equals(comp.get("status"))) {
         *         allHealthy = false;
         *         break;
         *     }
         * }
         */
        
        health.put("status", allHealthy ? "UP" : "DOWN");
        health.put("components", components);
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }

    /**
     * Simple ping endpoint
     * 
     * URL: GET /health/ping
     * 
     * Fastest health check - just returns "pong"
     * Used by load balancers for quick health checks
     * 
     * @return ResponseEntity<String> simple pong response
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
        
        /*
         * This is equivalent to lambda:
         * Function<HttpRequest, ResponseEntity<String>> pingHandler = 
         *     (request) -> ResponseEntity.ok("pong");
         */
    }
}