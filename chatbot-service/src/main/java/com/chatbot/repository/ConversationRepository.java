package com.chatbot.repository;

import com.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Conversation Repository - Handles database operations for conversations
 * 
 * Similar to MessageRepository but for Conversation entities
 * This follows the same Spring Data JPA patterns
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find all conversations for a specific user
     * 
     * Method naming: "findByUserId" automatically generates query
     * SQL query: SELECT * FROM conversations WHERE user_id = ?
     * 
     * @param userId ID of user from User Management Service
     * @return List<Conversation> all conversations for this user
     * 
     * Usage example:
     * List<Conversation> userConversations = conversationRepository.findByUserId(123L);
     */
    List<Conversation> findByUserId(Long userId);

    /**
     * Find active conversations for a user, ordered by most recent first
     * 
     * Complex method name breakdown:
     * - "findBy" - query method
     * - "UserIdAndIsActive" - filter by userId AND isActive fields
     * - "OrderBy" - sort results
     * - "UpdatedAtDesc" - sort by updatedAt field, descending (newest first)
     * 
     * @param userId User to find conversations for
     * @param isActive Whether to find active (true) or archived (false) conversations
     * @return List<Conversation> sorted by most recently updated
     * 
     * Usage example:
     * List<Conversation> activeChats = conversationRepository.findByUserIdAndIsActiveOrderByUpdatedAtDesc(123L, true);
     */
    List<Conversation> findByUserIdAndIsActiveOrderByUpdatedAtDesc(Long userId, Boolean isActive);

    /**
     * Find conversations by title (for search functionality)
     * 
     * "Containing" keyword enables partial text search
     * "IgnoreCase" makes search case-insensitive
     * 
     * SQL query: SELECT * FROM conversations WHERE user_id = ? AND LOWER(title) LIKE LOWER(?titlePart%)
     * 
     * @param userId User whose conversations to search
     * @param titlePart Partial title to search for
     * @return List<Conversation> conversations with matching titles
     * 
     * Usage example:
     * List<Conversation> reactChats = conversationRepository.findByUserIdAndTitleContainingIgnoreCase(123L, "react");
     */
    List<Conversation> findByUserIdAndTitleContainingIgnoreCase(Long userId, String titlePart);

    /*
     * LAMBDA EQUIVALENT EXPLANATION:
     * 
     * If you were doing these operations with Java streams and lambdas:
     * 
     * // findByUserId equivalent:
     * conversations.stream()
     *     .filter(conv -> conv.getUserId().equals(userId))
     *     .collect(Collectors.toList());
     * 
     * // findByUserIdAndIsActiveOrderByUpdatedAtDesc equivalent:
     * conversations.stream()
     *     .filter(conv -> conv.getUserId().equals(userId))           // Filter by user
     *     .filter(conv -> conv.isActive() == isActive)               // Filter by active status
     *     .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))  // Sort newest first
     *     .collect(Collectors.toList());
     * 
     * // findByUserIdAndTitleContainingIgnoreCase equivalent:
     * conversations.stream()
     *     .filter(conv -> conv.getUserId().equals(userId))
     *     .filter(conv -> conv.getTitle().toLowerCase().contains(titlePart.toLowerCase()))
     *     .collect(Collectors.toList());
     * 
     * But Spring Data eliminates all this boilerplate code!
     * You just declare method signatures, Spring implements them automatically.
     */
}