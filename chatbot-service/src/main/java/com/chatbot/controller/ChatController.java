package com.chatbot.controller;

import com.chatbot.model.Message;
import com.chatbot.model.Conversation;
import com.chatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Chat Controller - REST API Layer
 * 
 * Controller Layer Explanation:
 * - Handles HTTP requests from frontend (React app)
 * - Converts HTTP requests to Java method calls
 * - Converts Java objects back to JSON responses
 * - Defines API endpoints (URLs that frontend can call)
 * 
 * REST API Architecture:
 * Frontend (React) -> HTTP Request -> Controller -> Service -> Repository -> Database
 * Frontend (React) <- JSON Response <- Controller <- Service <- Repository <- Database
 * 
 * @RestController annotation combines:
 * - @Controller: Tells Spring this handles web requests
 * - @ResponseBody: Automatically converts return values to JSON
 * 
 * @RequestMapping: Base URL path for all endpoints in this controller
 */
@RestController
@RequestMapping("/api/chat")  // All endpoints start with /api/chat
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3001"})  // Allow React frontend to call these APIs
public class ChatController {

    /**
     * Inject ChatService to handle business logic
     * Controller doesn't do business logic - delegates to Service
     */
    @Autowired
    private ChatService chatService;

    /**
     * Send a message and get AI response
     * 
     * HTTP Method: POST (because we're creating/sending data)
     * URL: POST /api/chat/send
     * 
     * @PostMapping annotation:
     * - Maps HTTP POST requests to this method
     * - Automatically converts JSON request body to Java Map
     * - Automatically converts return value to JSON response
     * 
     * @RequestBody annotation:
     * - Tells Spring to read JSON from HTTP request body
     * - Converts JSON to Java Map automatically
     * 
     * Request JSON example:
     * {
     *   "conversationId": "conv123",
     *   "message": "Hello, how are you?",
     *   "model": "claude-3-5-sonnet"
     * }
     * 
     * @param request Map containing conversationId, message, model
     * @return ResponseEntity<Message> HTTP response with AI message
     * 
     * ResponseEntity explanation:
     * - Wrapper for HTTP response
     * - Contains response body (JSON data) + HTTP status code
     * - 200 OK for success, 400 Bad Request for errors, etc.
     */
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Map<String, String> request) {
        
        try {
            // Extract values from request JSON
            // Map.get() returns value for key, or null if key doesn't exist
            String conversationId = request.get("conversationId");
            String message = request.get("message");
            String model = request.get("model");
            
            // Validation - check required fields
            if (message == null || message.trim().isEmpty()) {
                // Return HTTP 400 Bad Request with error message
                return ResponseEntity.badRequest().build();
                
                /*
                 * ResponseEntity.badRequest() is equivalent to:
                 * return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                 * 
                 * Builder pattern makes it more readable
                 */
            }
            
            // Set default model if not provided
            if (model == null || model.trim().isEmpty()) {
                model = "claude-3-5-sonnet";
            }
            
            // Call service layer to handle business logic
            Message aiResponse = chatService.sendMessage(conversationId, message, model);
            
            // Return HTTP 200 OK with AI response as JSON
            return ResponseEntity.ok(aiResponse);
            
            /*
             * LAMBDA EQUIVALENT for validation:
             * 
             * return Optional.ofNullable(request.get("message"))
             *     .filter(msg -> !msg.trim().isEmpty())           // Check not empty
             *     .map(msg -> {                                   // If valid message
             *         String convId = request.get("conversationId");
             *         String modelName = Optional.ofNullable(request.get("model"))
             *             .orElse("claude-3-5-sonnet");           // Default model
             *         
             *         Message response = chatService.sendMessage(convId, msg, modelName);
             *         return ResponseEntity.ok(response);         // Return success
             *     })
             *     .orElse(ResponseEntity.badRequest().build());   // Return error if invalid
             */
            
        } catch (Exception e) {
            // Log error and return HTTP 500 Internal Server Error
            System.err.println("Error in sendMessage: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all messages in a conversation
     * 
     * HTTP Method: GET (because we're reading/retrieving data)
     * URL: GET /api/chat/conversations/{conversationId}/messages
     * 
     * @GetMapping annotation maps HTTP GET requests
     * @PathVariable annotation extracts {conversationId} from URL
     * 
     * URL example: GET /api/chat/conversations/conv123/messages
     * Spring extracts "conv123" and passes it as conversationId parameter
     * 
     * @param conversationId ID extracted from URL path
     * @return ResponseEntity<List<Message>> JSON array of messages
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<Message>> getConversationMessages(
            @PathVariable String conversationId) {
        
        try {
            // Get messages from service layer
            List<Message> messages = chatService.getConversationMessages(conversationId);
            
            // Return messages as JSON array
            return ResponseEntity.ok(messages);
            
            /*
             * JSON response example:
             * [
             *   {
             *     "id": "msg1",
             *     "conversationId": "conv123", 
             *     "role": "user",
             *     "content": "Hello",
             *     "timestamp": "2024-01-01T10:00:00"
             *   },
             *   {
             *     "id": "msg2",
             *     "conversationId": "conv123",
             *     "role": "assistant", 
             *     "content": "Hi there!",
             *     "model": "claude-3-5-sonnet",
             *     "timestamp": "2024-01-01T10:00:05"
             *   }
             * ]
             */
            
        } catch (Exception e) {
            System.err.println("Error getting messages: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a new conversation
     * 
     * HTTP Method: POST (creating new data)
     * URL: POST /api/chat/conversations
     * 
     * Request JSON example:
     * {
     *   "userId": 123,
     *   "title": "Help with React",
     *   "model": "claude-3-5-sonnet"
     * }
     * 
     * @param request Map containing userId, title, model
     * @return ResponseEntity<Conversation> created conversation with generated ID
     */
    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(@RequestBody Map<String, Object> request) {
        
        try {
            // Extract and convert values from request
            // request.get() returns Object, need to cast to specific types
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = (String) request.get("title");
            String model = (String) request.get("model");
            
            // Call service to create conversation
            Conversation conversation = chatService.createConversation(userId, title, model);
            
            // Return HTTP 201 Created with new conversation
            return ResponseEntity.status(201).body(conversation);
            
            /*
             * HTTP Status Codes explanation:
             * - 200 OK: Success, returning data
             * - 201 Created: Success, new resource created
             * - 400 Bad Request: Client error (invalid input)
             * - 500 Internal Server Error: Server error (our code failed)
             */
            
        } catch (NumberFormatException e) {
            // userId conversion failed
            System.err.println("Invalid userId format: " + e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            System.err.println("Error creating conversation: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        
        /*
         * LAMBDA EQUIVALENT for parameter extraction:
         * 
         * return Optional.ofNullable(request.get("userId"))
         *     .map(Object::toString)                          // Convert to string
         *     .map(userIdStr -> {
         *         try {
         *             return Long.valueOf(userIdStr);         // Convert to Long
         *         } catch (NumberFormatException e) {
         *             return null;                            // Invalid format
         *         }
         *     })
         *     .map(userId -> {                                // If valid userId
         *         String title = (String) request.get("title");
         *         String model = (String) request.get("model");
         *         Conversation conv = chatService.createConversation(userId, title, model);
         *         return ResponseEntity.status(201).body(conv);
         *     })
         *     .orElse(ResponseEntity.badRequest().build());   // If invalid userId
         */
    }

    /**
     * Get all conversations for a user
     * 
     * HTTP Method: GET (reading data)
     * URL: GET /api/chat/conversations?userId=123
     * 
     * @RequestParam annotation extracts query parameters from URL
     * Query parameter example: /api/chat/conversations?userId=123&active=true
     * 
     * @param userId User ID from query parameter
     * @return ResponseEntity<List<Conversation>> JSON array of conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getUserConversations(
            @RequestParam Long userId) {
        
        try {
            List<Conversation> conversations = chatService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
            
        } catch (Exception e) {
            System.err.println("Error getting conversations: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        
        /*
         * QUERY PARAMETER vs PATH VARIABLE:
         * 
         * Path Variable: /api/chat/conversations/{id}
         * - Used for identifying specific resource
         * - Example: GET /api/chat/conversations/conv123
         * 
         * Query Parameter: /api/chat/conversations?userId=123
         * - Used for filtering, pagination, optional parameters
         * - Example: GET /api/chat/conversations?userId=123&limit=10
         */
    }

    /**
     * Delete a conversation
     * 
     * HTTP Method: DELETE (removing data)
     * URL: DELETE /api/chat/conversations/{conversationId}
     * 
     * @param conversationId ID from URL path
     * @return ResponseEntity<Map> success/error message
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Map<String, Object>> deleteConversation(
            @PathVariable String conversationId) {
        
        try {
            boolean deleted = chatService.deleteConversation(conversationId);
            
            if (deleted) {
                // Return success message
                Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Conversation deleted successfully"
                );
                return ResponseEntity.ok(response);
                
            } else {
                // Return not found error
                Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Conversation not found"
                );
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting conversation: " + e.getMessage());
            
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Failed to delete conversation"
            );
            return ResponseEntity.internalServerError().body(response);
        }
        
        /*
         * LAMBDA EQUIVALENT for response building:
         * 
         * return chatService.deleteConversation(conversationId) 
         *     ? ResponseEntity.ok(Map.of("success", true, "message", "Deleted"))      // If deleted
         *     : ResponseEntity.status(404).body(Map.of("success", false, "message", "Not found"));  // If not found
         * 
         * Ternary operator (condition ? valueIfTrue : valueIfFalse) is like if-else
         */
    }

    /**
     * Health check endpoint for this controller
     * 
     * Simple endpoint to verify the chat service is working
     * Frontend can call this to check backend connectivity
     * 
     * URL: GET /api/chat/health
     * 
     * @return ResponseEntity<Map> status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        
        // Create response map with status information
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "ChatController",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(health);
        
        /*
         * JSON response:
         * {
         *   "status": "UP",
         *   "service": "ChatController", 
         *   "timestamp": "2024-01-01T10:00:00"
         * }
         */
    }

    /*
     * HTTP METHODS SUMMARY:
     * 
     * GET: Retrieve data (read-only, no side effects)
     * - GET /api/chat/conversations - Get user's conversations
     * - GET /api/chat/conversations/123/messages - Get messages
     * 
     * POST: Create new data or trigger actions
     * - POST /api/chat/send - Send message (creates new messages)
     * - POST /api/chat/conversations - Create new conversation
     * 
     * PUT: Update existing data (full replacement)
     * - PUT /api/chat/conversations/123 - Update entire conversation
     * 
     * PATCH: Partial update
     * - PATCH /api/chat/conversations/123 - Update conversation title only
     * 
     * DELETE: Remove data
     * - DELETE /api/chat/conversations/123 - Delete conversation
     * 
     * LAMBDA EQUIVALENT for HTTP method handling:
     * 
     * // Instead of multiple @GetMapping, @PostMapping methods
     * // You could use a single method with lambda routing:
     * 
     * Map<String, Function<HttpRequest, ResponseEntity>> routes = Map.of(
     *     "GET /health", (req) -> ResponseEntity.ok(Map.of("status", "UP")),
     *     "POST /send", (req) -> {
     *         Map<String, String> body = parseRequestBody(req);
     *         Message response = chatService.sendMessage(body.get("conversationId"), body.get("message"), body.get("model"));
     *         return ResponseEntity.ok(response);
     *     }
     * );
     * 
     * But Spring's annotation approach is much cleaner and more readable!
     */
}