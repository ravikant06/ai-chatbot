package com.chatbot.service;

import com.chatbot.model.Message;
import com.chatbot.model.Conversation;
import com.chatbot.repository.MessageRepository;
import com.chatbot.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Chat Service - Business Logic Layer
 * 
 * Service Layer Explanation:
 * - Contains business logic and rules
 * - Coordinates between Controller and Repository layers
 * - Handles complex operations that involve multiple entities
 * - Processes data before saving to database
 * 
 * @Service annotation tells Spring this is a business logic component
 * Spring will create a singleton instance (one object for entire application)
 * Other classes can inject this service using @Autowired
 */
@Service
public class ChatService {

    /**
     * Dependency Injection Explanation:
     * 
     * @Autowired tells Spring to automatically inject (provide) these dependencies
     * Instead of manually creating objects like:
     *   MessageRepository repo = new MessageRepositoryImpl();
     * 
     * Spring does this automatically:
     * 1. Creates MessageRepository implementation at startup
     * 2. Injects it into this service
     * 3. Manages object lifecycle (creation, destruction)
     * 
     * Benefits:
     * - Loose coupling (easy to swap implementations)
     * - Easy testing (can inject mock objects)
     * - No manual object management
     */
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private BedrockService bedrockService; // AWS AI service

    /**
     * Send a message and get AI response
     * 
     * This is the main business logic method that:
     * 1. Saves user message to database
     * 2. Calls AI service to get response
     * 3. Saves AI response to database
     * 4. Updates conversation metadata
     * 
     * @param conversationId Which conversation this message belongs to
     * @param userMessage The user's message text
     * @param model Which AI model to use (e.g., "claude-3-5-sonnet")
     * @return Message The AI's response message
     * 
     * Usage example:
     * Message response = chatService.sendMessage("conv123", "Hello", "claude-3-5-sonnet");
     */
    public Message sendMessage(String conversationId, String userMessage, String model) {
        
        // Step 1: Save user message to database
        Message userMsg = new Message(conversationId, "user", userMessage, null);
        userMsg.setTimestamp(LocalDateTime.now());
        
        // Repository.save() returns the saved object (with generated ID)
        Message savedUserMessage = messageRepository.save(userMsg);
        
        // Step 2: Get AI response from Bedrock service
        // This calls AWS Bedrock API to generate AI response
        String aiResponse = bedrockService.generateResponse(userMessage, model);
        
        // Step 3: Create and save AI response message
        Message aiMsg = new Message(conversationId, "assistant", aiResponse, model);
        aiMsg.setTimestamp(LocalDateTime.now());
        Message savedAiMessage = messageRepository.save(aiMsg);
        
        // Step 4: Update conversation metadata
        updateConversationTimestamp(conversationId);
        
        // Return the AI response to the controller
        return savedAiMessage;
        
        /*
         * LAMBDA EQUIVALENT (if doing manually):
         * 
         * // Save user message (repository does this automatically)
         * Message userMsg = messageList.stream()
         *     .filter(msg -> msg.getId() == null)  // Find unsaved message
         *     .findFirst()
         *     .map(msg -> {
         *         msg.setId(generateId());         // Set ID
         *         messageList.add(msg);            // Add to list
         *         return msg;                      // Return saved message
         *     })
         *     .orElse(null);
         * 
         * // But repository.save() does all this automatically!
         */
    }

    /**
     * Get all messages in a conversation
     * 
     * Simple delegation to repository layer
     * Service layer can add business logic here if needed
     * (e.g., filtering, pagination, access control)
     * 
     * @param conversationId ID of conversation to get messages for
     * @return List<Message> all messages in chronological order
     */
    public List<Message> getConversationMessages(String conversationId) {
        // Delegate to repository - could add business logic here later
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
        
        /*
         * LAMBDA EQUIVALENT:
         * 
         * return allMessages.stream()
         *     .filter(msg -> msg.getConversationId().equals(conversationId))
         *     .sorted(Comparator.comparing(Message::getTimestamp))
         *     .collect(Collectors.toList());
         */
    }

    /**
     * Create a new conversation
     * 
     * Business logic for conversation creation:
     * 1. Generate title from first message (if provided)
     * 2. Set default model
     * 3. Save to database
     * 
     * @param userId ID of user creating the conversation
     * @param title Optional title (can be null)
     * @param model AI model to use
     * @return Conversation the created conversation
     */
    public Conversation createConversation(Long userId, String title, String model) {
        
        // Business logic: Generate default title if none provided
        if (title == null || title.trim().isEmpty()) {
            title = "New Chat " + LocalDateTime.now().toString().substring(0, 16);
        }
        
        // Create new conversation object
        Conversation conversation = new Conversation(userId, title, model);
        
        // Save to database and return (with generated ID)
        return conversationRepository.save(conversation);
        
        /*
         * LAMBDA EQUIVALENT:
         * 
         * // Generate title if needed
         * String finalTitle = Optional.ofNullable(title)
         *     .filter(t -> !t.trim().isEmpty())           // Keep if not empty
         *     .orElse("New Chat " + LocalDateTime.now()); // Default if empty
         * 
         * // Create conversation
         * Conversation conv = new Conversation();
         * conv.setUserId(userId);
         * conv.setTitle(finalTitle);
         * conv.setModel(model);
         * 
         * But constructor + repository.save() is cleaner!
         */
    }

    /**
     * Get all conversations for a user
     * 
     * @param userId ID of user to get conversations for
     * @return List<Conversation> user's conversations (newest first)
     */
    public List<Conversation> getUserConversations(Long userId) {
        // Get active conversations, ordered by most recent
        return conversationRepository.findByUserIdAndIsActiveOrderByUpdatedAtDesc(userId, true);
    }

    /**
     * Delete a conversation and all its messages
     * 
     * Business logic for deletion:
     * 1. Delete all messages in conversation
     * 2. Delete the conversation itself
     * 3. Ensure data consistency
     * 
     * @param conversationId ID of conversation to delete
     * @return boolean true if deleted successfully, false if not found
     */
    public boolean deleteConversation(String conversationId) {
        
        try {
            Long convId = Long.valueOf(conversationId);
            
            // Check if conversation exists
            Optional<Conversation> conversation = conversationRepository.findById(convId);
            
            if (conversation.isPresent()) {
                // Step 1: Delete all messages in this conversation
                messageRepository.deleteByConversationId(conversationId);
                
                // Step 2: Delete the conversation itself
                conversationRepository.deleteById(convId);
                
                return true; // Successfully deleted
            }
            
            return false; // Conversation not found
        } catch (NumberFormatException e) {
            return false; // Invalid ID format
        }
        
        /*
         * LAMBDA EQUIVALENT:
         * 
         * // Check if exists and delete
         * return conversationRepository.findById(conversationId)
         *     .map(conv -> {                           // If conversation exists
         *         messageRepository.deleteByConversationId(conversationId);  // Delete messages
         *         conversationRepository.deleteById(conversationId);         // Delete conversation
         *         return true;                         // Return success
         *     })
         *     .orElse(false);                         // Return false if not found
         * 
         * This lambda approach is more functional programming style
         * But the if-else approach above is easier to understand for beginners
         */
    }

    /**
     * Helper method to update conversation timestamp
     * 
     * Private method (only used within this class)
     * Updates the "updatedAt" field when new messages are added
     * 
     * @param conversationId ID of conversation to update
     */
    private void updateConversationTimestamp(String conversationId) {
        
        try {
            Long convId = Long.valueOf(conversationId);
            
            // Optional<T> is Java's way of handling "might be null" values
            // Instead of risking NullPointerException, Optional forces you to handle missing values
            Optional<Conversation> optionalConversation = conversationRepository.findById(convId);
        
            // Check if conversation exists, then update it
            if (optionalConversation.isPresent()) {
                Conversation conversation = optionalConversation.get();
                conversation.setUpdatedAt(LocalDateTime.now());
                conversationRepository.save(conversation); // Save updated timestamp
            }
        } catch (NumberFormatException e) {
            // Invalid conversation ID format
            System.err.println("Invalid conversation ID: " + conversationId);
        }
        
        /*
         * LAMBDA EQUIVALENT using Optional:
         * 
         * conversationRepository.findById(conversationId)
         *     .ifPresent(conv -> {                    // If conversation exists
         *         conv.setUpdatedAt(LocalDateTime.now());  // Update timestamp
         *         conversationRepository.save(conv);       // Save changes
         *     });
         * 
         * This lambda version is more concise but does the same thing
         * ifPresent() only executes the lambda if Optional contains a value
         */
    }

    /**
     * Get conversation details by ID
     * 
     * @param conversationId ID of conversation to retrieve
     * @return Optional<Conversation> conversation if found, empty if not found
     * 
     * Optional<T> explanation:
     * - Container that may or may not contain a value
     * - Prevents NullPointerException
     * - Forces caller to handle "not found" case
     * 
     * Usage example:
     * Optional<Conversation> conv = chatService.getConversation("conv123");
     * if (conv.isPresent()) {
     *     Conversation conversation = conv.get();
     *     // Use conversation
     * } else {
     *     // Handle not found
     * }
     */
    public Optional<Conversation> getConversation(String conversationId) {
        try {
            Long convId = Long.valueOf(conversationId);
            return conversationRepository.findById(convId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}